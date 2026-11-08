package com.kaanf.home.presentation.profileboard

import com.kaanf.core.domain.repository.MovieRepository

import com.kaanf.core.domain.model.movie.Movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.designsystem.profileboard.ProfileBoardEditor
import com.kaanf.core.designsystem.profileboard.ProfileBoardState
import com.kaanf.core.domain.model.music.MusicTrack
import com.kaanf.core.domain.model.profileboard.ProfileBoard
import com.kaanf.core.domain.repository.MusicRepository
import com.kaanf.core.domain.repository.ProfileBoardRepository
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.profile_board_notification_title
import crew.feature.home.presentation.generated.resources.profile_board_load_error
import crew.feature.home.presentation.generated.resources.profile_board_save_error
import crew.feature.home.presentation.generated.resources.profile_board_conflict
import crew.feature.home.presentation.generated.resources.profile_board_unsupported
import crew.feature.home.presentation.generated.resources.profile_board_qr_image_error
import crew.feature.home.presentation.generated.resources.profile_board_song_save_error
import crew.feature.home.presentation.generated.resources.profile_board_movie_save_error
import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileBoardViewModel(
    private val userRepository: UserRepository,
    private val boardRepository: ProfileBoardRepository,
    private val movieRepository: MovieRepository,
    private val musicRepository: MusicRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileBoardState())
    val state = _state.asStateFlow()
    private var savedBoard = ProfileBoard()
    private var editingBaseline: ProfileBoardState? = null
    private var draftRevision = 0L
    private var isRefreshing = false
    private var hasLoadedBoard = false
    private val recoveredQrImages = mutableSetOf<String>()
    private val market = Locale.current.region.takeIf { it.length == 2 } ?: FALLBACK_MARKET
    private val movieLanguage = Locale.current.language.takeIf { it.matches(Regex("[a-z]{2}")) } ?: "en"
    private var movieSearchJob: Job? = null
    private var songSearchJob: Job? = null

    init {
        userRepository.observeCurrentUser().onEach { user ->
            _state.update { it.copy(
                publicCode = user?.publicCode.orEmpty(),
                fullName = user?.fullName.orEmpty(),
                profilePictureUrl = user?.profilePictureUrl,
            ) }
        }.launchIn(viewModelScope)
        boardRepository.observeBoard().onEach { board ->
            if (board != null) {
                savedBoard = board
                // A remote refresh must never silently replace an active editing draft.
                if (!_state.value.isEditingBoard && !_state.value.isSaving) applyBoard(board)
            }
        }.launchIn(viewModelScope)
        boardRepository.observeQrRender().onEach { render ->
            _state.update { it.copy(qrRender = render) }
        }.launchIn(viewModelScope)
        state.map { !it.isLoading && !it.canEdit && !it.hasLoadError }
            .distinctUntilChanged().onEach { unsupported ->
                if (unsupported) notify(Res.string.profile_board_unsupported, SnackbarVariant.Warn)
            }.launchIn(viewModelScope)
    }

    fun onAction(action: ProfileBoardAction) {
        if (_state.value.isSaving || _state.value.movieSearch.isSaving) return
        when (action) {
            is ProfileBoardAction.LayoutChanged -> _state.update { ProfileBoardEditor.reduce(it, action) }
            ProfileBoardAction.FinishEditing -> save()
            ProfileBoardAction.CancelEditing -> {
                _state.update { it.copy(isEditingBoard = false, pickerSlot = null, focusedStickerSlot = null,
                    hasSaveError = false, hasConflict = false, isEditorSheetOpen = false, openTool = null) }
                applyBoard(savedBoard)
            }
            ProfileBoardAction.ReloadBoard -> {
                onAction(ProfileBoardAction.CancelEditing)
                refresh()
            }
            is ProfileBoardAction.SearchMovies -> searchMovies(action.query)
            ProfileBoardAction.LoadMoreMovies -> loadMoreMovies()
            is ProfileBoardAction.ApplyMovie -> applyMovie(action.movie)
            ProfileBoardAction.RemoveMovie -> removeMovie()
            is ProfileBoardAction.SearchSongs -> searchSongs(action.query)
            ProfileBoardAction.LoadMoreSongs -> loadMoreSongs()
            is ProfileBoardAction.ApplySong -> applySong(action.track)
            ProfileBoardAction.RemoveSong -> removeSong()
            else -> {
                if (!_state.value.canEdit || _state.value.isLoading || _state.value.hasLoadError || _state.value.hasConflict) return
                if (!_state.value.isEditingBoard &&
                    (action == ProfileBoardAction.EditBoard || action is ProfileBoardAction.OpenStickerPicker)) {
                    draftRevision = savedBoard.revision
                    editingBaseline = ProfileBoardEditor.settleBoard(_state.value)
                }
                _state.update { ProfileBoardEditor.reduce(it, action).copy(hasSaveError = false) }
            }
        }
    }

    private fun applyBoard(board: ProfileBoard) {
        _state.update { it.copy(hobbies = board.hobbies, stickers = board.stickers, qrStyleId = board.qrStyleId, canEdit = board.canEdit,
            qr = board.qr, boardLayout = it.boardLayout.copy(qr = board.qr.bounds())) }
    }

    private fun save() {
        val current = ProfileBoardEditor.settleBoard(_state.value)
        if (!current.isEditingBoard || current.hasConflict || !current.canEdit) return
        val baseline = editingBaseline?.let {
            ProfileBoardEditor.settleBoard(it.copy(boardLayout = current.boardLayout))
        }
        if (baseline != null && ProfileBoardEditor.hasSameContent(current, baseline)) {
            onAction(ProfileBoardAction.CancelEditing)
            return
        }
        val draft = ProfileBoard(hobbies = current.hobbies, revision = draftRevision, qrStyleId = current.qrStyleId, qr = current.qr, stickers = current.stickers.map {
            com.kaanf.core.domain.model.profileboard.BoardStickerPlacement.constrain(it, current.boardLayout)
        })
        _state.update { it.copy(isSaving = true, hasSaveError = false, pickerSlot = null, isEditorSheetOpen = false, openTool = null, stickers = draft.stickers, qr = draft.qr, boardLayout = current.boardLayout) }
        viewModelScope.launch {
            when (val result = boardRepository.save(draft)) {
                is Result.Success -> {
                    savedBoard = result.data
                    _state.update { it.copy(isSaving = false, isEditingBoard = false,
                        focusedStickerSlot = null, hasSaveError = false, hasConflict = false, isEditorSheetOpen = false) }
                    applyBoard(result.data)
                }
                is Result.Failure -> {
                    val conflict = (result.error as? DataError.Remote.Business)?.code == "PROFILE_BOARD_CONFLICT"
                    _state.update { it.copy(isSaving = false, hasSaveError = !conflict, hasConflict = conflict) }
                    notify(if (conflict) Res.string.profile_board_conflict else Res.string.profile_board_save_error)
                }
            }
        }
    }

    fun refresh() {
        if (isRefreshing || _state.value.isEditingBoard || _state.value.isSaving) return
        isRefreshing = true
        _state.update { it.copy(isLoading = !hasLoadedBoard, hasLoadError = false) }
        viewModelScope.launch {
            val user = userRepository.getUser()
            launch { loadFavoriteSong() }
            launch { loadFavoriteMovie() }
            val board = boardRepository.refresh()
            if (board is Result.Success) {
                savedBoard = board.data
                hasLoadedBoard = true
                applyBoard(board.data)
                launch { boardRepository.syncQrRender() }
            }
            _state.update { it.copy(isLoading = false, hasLoadError = user is Result.Failure || board is Result.Failure) }
            isRefreshing = false
            if (_state.value.hasLoadError) notify(Res.string.profile_board_load_error)
        }
    }

    private suspend fun loadFavoriteSong() {
        // A failed lookup keeps the last known song; the tile simply stays as it was.
        val result = musicRepository.getFavorite(market)
        if (result is Result.Success) _state.update { it.copy(favoriteTrack = result.data) }
    }

    private fun searchSongs(query: String) {
        songSearchJob?.cancel()
        val trimmed = query.trim()
        _state.update { it.copy(songSearch = it.songSearch.copy(query = query, hasError = false,
            tracks = if (trimmed.length < MIN_SONG_QUERY) emptyList() else it.songSearch.tracks,
            nextOffset = null, isLoading = trimmed.length >= MIN_SONG_QUERY)) }
        if (trimmed.length < MIN_SONG_QUERY) return
        songSearchJob = viewModelScope.launch {
            delay(SONG_SEARCH_DEBOUNCE_MS)
            fetchSongs(trimmed, offset = 0)
        }
    }

    private fun loadMoreSongs() {
        val search = _state.value.songSearch
        val offset = search.nextOffset ?: return
        if (search.isLoading) return
        _state.update { it.copy(songSearch = it.songSearch.copy(isLoading = true, hasError = false)) }
        songSearchJob = viewModelScope.launch { fetchSongs(search.query.trim(), offset) }
    }

    private suspend fun fetchSongs(query: String, offset: Int) {
        when (val result = musicRepository.search(query, market, offset)) {
            is Result.Success -> _state.update { current ->
                val tracks = if (offset == 0) result.data.tracks else current.songSearch.tracks + result.data.tracks
                current.copy(songSearch = current.songSearch.copy(tracks = tracks.distinctBy { it.id },
                    nextOffset = result.data.nextOffset, isLoading = false))
            }
            is Result.Failure -> _state.update { it.copy(songSearch = it.songSearch.copy(isLoading = false, hasError = true)) }
        }
    }

    private fun applySong(track: MusicTrack) {
        if (!_state.value.isEditingBoard || _state.value.songSearch.isSaving) return
        _state.update { it.copy(songSearch = it.songSearch.copy(isSaving = true)) }
        viewModelScope.launch {
            when (val result = musicRepository.saveFavorite(track.id, market)) {
                is Result.Success -> _state.update { it.copy(favoriteTrack = result.data, openTool = null,
                    songSearch = it.songSearch.copy(isSaving = false)) }
                is Result.Failure -> {
                    _state.update { it.copy(songSearch = it.songSearch.copy(isSaving = false)) }
                    notify(Res.string.profile_board_song_save_error)
                }
            }
        }
    }

    private fun removeSong() {
        val previous = _state.value.favoriteTrack ?: return
        if (!_state.value.isEditingBoard) return
        _state.update { it.copy(favoriteTrack = null) }
        viewModelScope.launch {
            if (musicRepository.clearFavorite() is Result.Failure) {
                _state.update { it.copy(favoriteTrack = it.favoriteTrack ?: previous) }
                notify(Res.string.profile_board_song_save_error)
            }
        }
    }

    private suspend fun loadFavoriteMovie() {
        // A failed lookup keeps the last known movie; the tile simply stays as it was.
        val result = movieRepository.getFavorite(movieLanguage)
        if (result is Result.Success) _state.update {
            if (it.isEditingBoard || it.movieSearch.isSaving) it else it.copy(favoriteMovie = result.data)
        }
    }

    private fun searchMovies(query: String) {
        movieSearchJob?.cancel()
        val trimmed = query.trim()
        _state.update { it.copy(movieSearch = it.movieSearch.copy(query = query, hasError = false,
            movies = emptyList(),
            nextPage = null, isLoading = trimmed.length >= MIN_MOVIE_QUERY)) }
        if (trimmed.length < MIN_MOVIE_QUERY) return
        movieSearchJob = viewModelScope.launch {
            delay(MOVIE_SEARCH_DEBOUNCE_MS)
            fetchMovies(trimmed, page = 1)
        }
    }

    private fun loadMoreMovies() {
        val search = _state.value.movieSearch
        val page = search.nextPage ?: return
        if (search.isLoading) return
        _state.update { it.copy(movieSearch = it.movieSearch.copy(isLoading = true, hasError = false)) }
        movieSearchJob = viewModelScope.launch { fetchMovies(search.query.trim(), page) }
    }

    private suspend fun fetchMovies(query: String, page: Int) {
        when (val result = movieRepository.search(query, movieLanguage, page)) {
            is Result.Success -> _state.update { current ->
                if (current.movieSearch.query.trim() != query) return@update current
                val movies = if (page == 1) result.data.movies else current.movieSearch.movies + result.data.movies
                current.copy(movieSearch = current.movieSearch.copy(movies = movies.distinctBy { it.id },
                    nextPage = result.data.nextPage, isLoading = false))
            }
            is Result.Failure -> _state.update {
                if (it.movieSearch.query.trim() != query) it
                else it.copy(movieSearch = it.movieSearch.copy(isLoading = false, hasError = true))
            }
        }
    }

    private fun applyMovie(movie: Movie) {
        if (!_state.value.isEditingBoard || _state.value.movieSearch.isSaving) return
        _state.update { it.copy(movieSearch = it.movieSearch.copy(isSaving = true)) }
        viewModelScope.launch {
            when (val result = movieRepository.saveFavorite(movie.id, movieLanguage)) {
                is Result.Success -> _state.update { it.copy(favoriteMovie = result.data, openTool = null,
                    movieSearch = it.movieSearch.copy(isSaving = false)) }
                is Result.Failure -> {
                    _state.update { it.copy(movieSearch = it.movieSearch.copy(isSaving = false)) }
                    notify(Res.string.profile_board_movie_save_error)
                }
            }
        }
    }

    private fun removeMovie() {
        if (!_state.value.isEditingBoard || _state.value.favoriteMovie == null || _state.value.movieSearch.isSaving) return
        _state.update { it.copy(movieSearch = it.movieSearch.copy(isSaving = true)) }
        viewModelScope.launch {
            when (movieRepository.clearFavorite()) {
                is Result.Success -> _state.update { it.copy(favoriteMovie = null,
                    movieSearch = it.movieSearch.copy(isSaving = false)) }
                is Result.Failure -> {
                    _state.update { it.copy(movieSearch = it.movieSearch.copy(isSaving = false)) }
                    notify(Res.string.profile_board_movie_save_error)
                }
            }
        }
    }

    fun onQrImageError() {
        val failedUrl = _state.value.qrRender?.imageUrl
        viewModelScope.launch {
            // Recheck status once per failed image; the server repairs missing files during GET.
            if (failedUrl != null && recoveredQrImages.add(failedUrl)) {
                val result = boardRepository.syncQrRender()
                if (result is Result.Success && result.data.imageUrl != failedUrl) return@launch
            }
            notify(Res.string.profile_board_qr_image_error)
        }
    }

    private suspend fun notify(description: StringResource, variant: SnackbarVariant = SnackbarVariant.Error) {
        snackbarController.show(SnackbarMessage(
            title = UIText.Resource(Res.string.profile_board_notification_title),
            description = UIText.Resource(description),
            variant = variant,
        ))
    }

    private companion object {
        const val MIN_MOVIE_QUERY = 2
        const val MOVIE_SEARCH_DEBOUNCE_MS = 300L
        const val FALLBACK_MARKET = "US"
        const val MIN_SONG_QUERY = 2
        const val SONG_SEARCH_DEBOUNCE_MS = 300L
    }
}

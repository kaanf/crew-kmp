package com.kaanf.game.presentation.quests

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.mediapicker.encodeJpeg
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.domain.model.EventParticipant
import com.kaanf.game.domain.model.Quest
import com.kaanf.game.domain.model.QuestPhotoTag
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class QuestsState(
    val isLoading: Boolean = true,
    val quests: List<Quest> = emptyList(),
    val claimingKey: String? = null,
    /** Foto questlerine gönderilmiş fotoğraf: questKey → fotoğraf. */
    val photos: Map<String, EventMemory> = emptyMap(),
    /** Etiketlenebilecek kişiler (check-in'li, ben hariç); ilk foto çekiminde çekilir. */
    val taggableParticipants: List<EventParticipant> = emptyList(),
    val isUploading: Boolean = false,
)

class QuestsViewModel(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private var currentUserId: String? = null

    private val _state = MutableStateFlow(QuestsState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadQuests()
        loadPhotos()
        userRepository.observeCurrentUser()
            .onEach { currentUserId = it?.id }
            .launchIn(viewModelScope)
    }

    private fun loadQuests() {
        viewModelScope.launch {
            matchRepository.getQuests(eventId)
                .onSuccess { quests ->
                    _state.update {
                        // Foto questleri katalogun sonunda; listede öne alınır ki yeni akış görünsün.
                        it.copy(isLoading = false, quests = quests.sortedByDescending(Quest::isPhoto))
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    /**
     * Foto questlerinin karesi: benim yüklediklerim + etiketlendiklerim. Tek sayfa yeter,
     * her queste kendi fotoğrafım varsa o gösterilir (aynı queste birden çok kişi beni
     * etiketlemişse ilk gelen kare gösterilir; hata değil, sadece bir kare gösteriliyor).
     */
    private fun loadPhotos() {
        viewModelScope.launch {
            matchRepository.getMemories(eventId, page = 0, size = PHOTO_PAGE_SIZE)
                .onSuccess { memories -> _state.update { it.copy(photos = memories.byQuestKey()) } }
                // Sessiz düşer: quest listesi fotoğrafsız da çalışır, ikinci bir snackbar gürültü olur.
                .onFailure { }
        }
    }

    /** Etiket seçicisinin kaynağı; bir kez çekilir, kamera açılırken tetiklenir. */
    fun loadTaggableParticipants() {
        if (_state.value.taggableParticipants.isNotEmpty()) return
        viewModelScope.launch {
            matchRepository.getEventParticipants(eventId)
                .onSuccess { participants ->
                    _state.update {
                        it.copy(
                            taggableParticipants = participants.filter { participant ->
                                participant.isCheckedIn && participant.userId != currentUserId
                            },
                        )
                    }
                }
                .onFailure { error -> snackbarController.show(error.toSnackbarMessage()) }
        }
    }

    /**
     * [image] etiket sheet'inin gösterdiği karedir (EXIF-upright, 1600px'e küçültülmüş);
     * pin oranları o kareye göre hesaplandığı için yüklenen de aynı kare olmalı. Format
     * JPEG, çünkü sunucunun mime whitelist'i jpeg/png.
     */
    fun submitPhoto(
        questKey: String,
        tags: List<QuestPhotoTag>,
        image: ImageBitmap,
        onSent: () -> Unit,
    ) {
        if (_state.value.isUploading) return
        _state.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            matchRepository.uploadQuestPhoto(
                eventId = eventId,
                questKey = questKey,
                tags = tags,
                imageBytes = encodeJpeg(image),
                mimeType = "image/jpeg",
            )
                .onSuccess { memory ->
                    // Sunucu artık questi tamamlanmış sayar; claim satırı için listeyi
                    // yeniden çekmeye gerek yok, aynı sonucu yerelde kuruyoruz.
                    _state.update { current ->
                        current.copy(
                            isUploading = false,
                            photos = current.photos + (questKey to memory),
                            quests = current.quests.map { quest ->
                                if (quest.key == questKey) {
                                    quest.copy(progress = quest.target, completed = true)
                                } else {
                                    quest
                                }
                            },
                        )
                    }
                    onSent()
                }
                .onFailure { error ->
                    _state.update { it.copy(isUploading = false) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    fun claim(questKey: String) {
        if (_state.value.claimingKey != null) return
        _state.update { it.copy(claimingKey = questKey) }
        viewModelScope.launch {
            matchRepository.claimQuest(eventId, questKey)
                .onSuccess { claimed ->
                    _state.update { current ->
                        current.copy(
                            claimingKey = null,
                            quests = current.quests.map { if (it.key == claimed.key) claimed else it },
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(claimingKey = null) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    private fun List<EventMemory>.byQuestKey(): Map<String, EventMemory> =
        filter { it.questKey != null }
            // associateBy'da aynı key için son kayıt kazanır; benimki sona kalsın.
            .sortedBy { it.isMine }
            .associateBy { it.questKey!! }

    private companion object {
        // ponytail: tek sayfa. Foto quest sayısı bir elin parmağı kadar; sayfalama
        // gerekirse (aynı queste onlarca etiket) buradan sayfa döngüsüne çıkılır.
        const val PHOTO_PAGE_SIZE = 50
    }
}

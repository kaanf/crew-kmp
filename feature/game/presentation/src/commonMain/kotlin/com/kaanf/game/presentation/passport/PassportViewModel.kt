package com.kaanf.game.presentation.passport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import com.kaanf.game.domain.model.AddressBook
import com.kaanf.game.domain.model.AddressBookEntry
import com.kaanf.game.domain.repository.MatchRepository
import com.kaanf.game.presentation.util.toClockText
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.passport_claim_check_in_description
import crew.feature.game.presentation.generated.resources.passport_claim_check_in_title
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class PassportViewModel(
    private val matchRepository: MatchRepository,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private val _state = MutableStateFlow(PassportState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadPassport()
    }

    private fun loadPassport() {
        viewModelScope.launch {
            matchRepository.getAddressBook(eventId)
                .onSuccess { book -> _state.update { it.copy(isLoading = false).applying(book) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    /** Seçili damganın tanışma puanını alır; sunucudan dönen güncel defterle state değişir. */
    fun claim(userId: String) {
        if (_state.value.claimingId != null || _state.value.isClaimingAll) return
        _state.update { it.copy(claimingId = userId) }
        viewModelScope.launch {
            matchRepository.claimMeeting(eventId, userId)
                .onSuccess { book -> _state.update { it.copy(claimingId = null).applying(book) } }
                .onFailure { error ->
                    _state.update { it.copy(claimingId = null) }
                    snackbarController.show(error.toClaimSnackbarMessage())
                }
        }
    }

    /** Bekleyen tüm tanışma puanlarını tek istekte alır. */
    fun claimAll() {
        if (_state.value.claimingId != null || _state.value.isClaimingAll) return
        _state.update { it.copy(isClaimingAll = true) }
        viewModelScope.launch {
            matchRepository.claimAllMeetings(eventId)
                .onSuccess { book -> _state.update { it.copy(isClaimingAll = false).applying(book) } }
                .onFailure { error ->
                    _state.update { it.copy(isClaimingAll = false) }
                    snackbarController.show(error.toClaimSnackbarMessage())
                }
        }
    }
}

/**
 * Sunucu check-in'siz claim'i PARTICIPANT_NOT_AVAILABLE ile düşürür — merkezi eşlemede o kod
 * "rakip maça müsait değil" demek, burada "mekâna check-in yapmadın". Aynı kod iki ayrı anlam
 * taşıdığı için claim bağlamında mesajı ekran veriyor; kalan tüm kodlar merkezden gelir.
 */
private fun DataError.Remote.toClaimSnackbarMessage(): SnackbarMessage =
    if (this is DataError.Remote.Business && code == NOT_CHECKED_IN_CODE) {
        SnackbarMessage(
            title = UIText.Resource(Res.string.passport_claim_check_in_title),
            description = UIText.Resource(Res.string.passport_claim_check_in_description),
            variant = SnackbarVariant.Warn,
        )
    } else {
        toSnackbarMessage()
    }

private const val NOT_CHECKED_IN_CODE = "PARTICIPANT_NOT_AVAILABLE"

private fun PassportState.applying(book: AddressBook) = copy(
    totalSlots = book.totalCount,
    // En eski tanışma önce: damgalar sayfaya basıldıkları sırayla dizilir.
    stamps = book.entries.sortedBy { entry -> entry.metAt }.map(::toStampUi),
    hostStampCollected = book.entries.any { entry -> entry.title != null },
)

// Damganın görsel kimliği kişiye özel ve kalıcı: userId hash'inden türer, her açılışta aynıdır.
private val StampInks = listOf(
    AccessDefaults.Coral,
    AccessDefaults.Sky,
    AccessDefaults.Teal,
    AccessDefaults.Amber,
    AccessDefaults.Mint,
    AccessDefaults.Rose,
    AccessDefaults.Accent,
)

private fun toStampUi(entry: AddressBookEntry): PassportStampUi {
    val hash = entry.userId.hashCode()
    val positive = abs(hash)
    return PassportStampUi(
        id = entry.userId,
        ownerName = entry.fullName,
        initial = entry.title?.emoji
            ?: entry.fullName.trim().firstOrNull()?.uppercase().orEmpty(),
        ink = if (entry.title != null) {
            AccessDefaults.Amber
        } else {
            StampInks[positive % StampInks.size]
        },
        shape = StampShape.entries[(positive / StampInks.size) % StampShape.entries.size],
        rotationDegrees = (hash % 9).toFloat(),
        collectedAt = entry.metAt.toClockText(),
        firstMatchWon = entry.firstMatchWon,
        firstMatchTaskTitle = entry.firstMatchTaskTitle,
        isRare = entry.title != null,
        points = entry.points,
        claimed = entry.claimed,
    )
}

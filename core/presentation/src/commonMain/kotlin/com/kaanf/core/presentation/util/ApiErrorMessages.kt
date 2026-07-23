package com.kaanf.core.presentation.util

import com.kaanf.core.presentation.snackbar.SnackbarVariant
import crew.core.presentation.generated.resources.Res
import crew.core.presentation.generated.resources.err_conflicting_result_desc
import crew.core.presentation.generated.resources.err_conflicting_result_title
import crew.core.presentation.generated.resources.err_duplicate_match_invite_desc
import crew.core.presentation.generated.resources.err_duplicate_match_invite_title
import crew.core.presentation.generated.resources.err_email_not_verified_desc
import crew.core.presentation.generated.resources.err_email_not_verified_title
import crew.core.presentation.generated.resources.err_event_full_desc
import crew.core.presentation.generated.resources.err_event_full_title
import crew.core.presentation.generated.resources.err_event_not_found_desc
import crew.core.presentation.generated.resources.err_event_not_found_title
import crew.core.presentation.generated.resources.err_event_not_in_gameplay_desc
import crew.core.presentation.generated.resources.err_event_not_in_gameplay_title
import crew.core.presentation.generated.resources.err_event_not_open_entry_desc
import crew.core.presentation.generated.resources.err_event_not_open_entry_title
import crew.core.presentation.generated.resources.err_event_not_open_tickets_desc
import crew.core.presentation.generated.resources.err_event_not_open_tickets_title
import crew.core.presentation.generated.resources.err_invalid_entry_code_desc
import crew.core.presentation.generated.resources.err_invalid_entry_code_title
import crew.core.presentation.generated.resources.err_invalid_match_qr_desc
import crew.core.presentation.generated.resources.err_invalid_match_qr_title
import crew.core.presentation.generated.resources.err_invalid_profile_picture_desc
import crew.core.presentation.generated.resources.err_invalid_profile_picture_title
import crew.core.presentation.generated.resources.err_match_invite_not_found_desc
import crew.core.presentation.generated.resources.err_match_invite_not_found_title
import crew.core.presentation.generated.resources.err_match_invite_not_pending_desc
import crew.core.presentation.generated.resources.err_match_invite_not_pending_title
import crew.core.presentation.generated.resources.err_match_not_cancellable_desc
import crew.core.presentation.generated.resources.err_match_not_cancellable_title
import crew.core.presentation.generated.resources.err_match_not_completed_desc
import crew.core.presentation.generated.resources.err_match_not_completed_title
import crew.core.presentation.generated.resources.err_match_not_found_desc
import crew.core.presentation.generated.resources.err_match_not_found_title
import crew.core.presentation.generated.resources.err_match_not_ready_waiting_desc
import crew.core.presentation.generated.resources.err_match_not_ready_waiting_title
import crew.core.presentation.generated.resources.err_match_not_result_phase_desc
import crew.core.presentation.generated.resources.err_match_not_result_phase_title
import crew.core.presentation.generated.resources.err_match_not_task_active_desc
import crew.core.presentation.generated.resources.err_match_not_task_active_title
import crew.core.presentation.generated.resources.err_match_not_task_offer_desc
import crew.core.presentation.generated.resources.err_match_not_task_offer_title
import crew.core.presentation.generated.resources.err_match_not_task_pick_desc
import crew.core.presentation.generated.resources.err_match_not_task_pick_title
import crew.core.presentation.generated.resources.err_participant_not_available_desc
import crew.core.presentation.generated.resources.err_participant_not_available_title
import crew.core.presentation.generated.resources.err_participant_not_found_desc
import crew.core.presentation.generated.resources.err_participant_not_found_title
import crew.core.presentation.generated.resources.err_rate_limit_desc
import crew.core.presentation.generated.resources.err_rate_limit_title
import crew.core.presentation.generated.resources.err_same_password_desc
import crew.core.presentation.generated.resources.err_same_password_title
import crew.core.presentation.generated.resources.err_self_match_invite_desc
import crew.core.presentation.generated.resources.err_self_match_invite_title
import crew.core.presentation.generated.resources.err_task_not_allowed_phase_desc
import crew.core.presentation.generated.resources.err_task_not_allowed_phase_title
import crew.core.presentation.generated.resources.err_task_not_found_desc
import crew.core.presentation.generated.resources.err_task_not_found_title
import crew.core.presentation.generated.resources.err_ticket_already_exists_desc
import crew.core.presentation.generated.resources.err_ticket_already_exists_title
import crew.core.presentation.generated.resources.err_ticket_already_used_desc
import crew.core.presentation.generated.resources.err_ticket_already_used_title
import crew.core.presentation.generated.resources.err_ticket_event_mismatch_desc
import crew.core.presentation.generated.resources.err_ticket_event_mismatch_title
import crew.core.presentation.generated.resources.err_ticket_not_active_desc
import crew.core.presentation.generated.resources.err_ticket_not_active_title
import crew.core.presentation.generated.resources.err_ticket_not_found_desc
import crew.core.presentation.generated.resources.err_ticket_not_found_title
import crew.core.presentation.generated.resources.err_user_exists_desc
import crew.core.presentation.generated.resources.err_user_exists_title
import crew.core.presentation.generated.resources.err_user_not_found_desc
import crew.core.presentation.generated.resources.err_user_not_found_title
import org.jetbrains.compose.resources.StringResource

/** Bir backend hata code'unun kullanıcıya gösterilecek başlık/açıklama/varyant karşılığı. */
data class ApiErrorUi(
    val title: StringResource,
    val description: StringResource,
    val variant: SnackbarVariant,
)

/**
 * Backend'in {code} değerini lokalize başlık + mesaja çevirir. Buraya eklenmeyen (altyapı/admin)
 * code'lar için null döner; çağıran taraf backend'in ham [message]'ına düşer. Yeni bir code
 * eklemek = buraya bir satır + strings.xml'e iki string.
 */
fun apiErrorUi(code: String): ApiErrorUi? = when (code) {
    "USER_EXISTS" -> ApiErrorUi(Res.string.err_user_exists_title, Res.string.err_user_exists_desc, SnackbarVariant.Warn)
    "USER_NOT_FOUND" -> ApiErrorUi(Res.string.err_user_not_found_title, Res.string.err_user_not_found_desc, SnackbarVariant.Error)
    "EMAIL_NOT_VERIFIED" -> ApiErrorUi(Res.string.err_email_not_verified_title, Res.string.err_email_not_verified_desc, SnackbarVariant.Warn)
    "SAME_PASSWORD" -> ApiErrorUi(Res.string.err_same_password_title, Res.string.err_same_password_desc, SnackbarVariant.Warn)
    "INVALID_PROFILE_PICTURE" -> ApiErrorUi(Res.string.err_invalid_profile_picture_title, Res.string.err_invalid_profile_picture_desc, SnackbarVariant.Error)
    "RATE_LIMIT_EXCEEDED" -> ApiErrorUi(Res.string.err_rate_limit_title, Res.string.err_rate_limit_desc, SnackbarVariant.Warn)

    "EVENT_NOT_FOUND" -> ApiErrorUi(Res.string.err_event_not_found_title, Res.string.err_event_not_found_desc, SnackbarVariant.Error)
    "TICKET_NOT_FOUND" -> ApiErrorUi(Res.string.err_ticket_not_found_title, Res.string.err_ticket_not_found_desc, SnackbarVariant.Error)
    "TICKET_ALREADY_EXISTS" -> ApiErrorUi(Res.string.err_ticket_already_exists_title, Res.string.err_ticket_already_exists_desc, SnackbarVariant.Warn)
    "EVENT_FULL" -> ApiErrorUi(Res.string.err_event_full_title, Res.string.err_event_full_desc, SnackbarVariant.Warn)
    "EVENT_NOT_OPEN_FOR_TICKETS" -> ApiErrorUi(Res.string.err_event_not_open_tickets_title, Res.string.err_event_not_open_tickets_desc, SnackbarVariant.Warn)
    "EVENT_NOT_OPEN_FOR_ENTRY" -> ApiErrorUi(Res.string.err_event_not_open_entry_title, Res.string.err_event_not_open_entry_desc, SnackbarVariant.Warn)
    "TICKET_EVENT_MISMATCH" -> ApiErrorUi(Res.string.err_ticket_event_mismatch_title, Res.string.err_ticket_event_mismatch_desc, SnackbarVariant.Error)
    "TICKET_ALREADY_USED" -> ApiErrorUi(Res.string.err_ticket_already_used_title, Res.string.err_ticket_already_used_desc, SnackbarVariant.Warn)
    "TICKET_NOT_ACTIVE" -> ApiErrorUi(Res.string.err_ticket_not_active_title, Res.string.err_ticket_not_active_desc, SnackbarVariant.Error)
    "INVALID_ENTRY_CODE" -> ApiErrorUi(Res.string.err_invalid_entry_code_title, Res.string.err_invalid_entry_code_desc, SnackbarVariant.Error)

    "EVENT_NOT_IN_GAMEPLAY" -> ApiErrorUi(Res.string.err_event_not_in_gameplay_title, Res.string.err_event_not_in_gameplay_desc, SnackbarVariant.Warn)
    "INVALID_MATCH_QR_TOKEN" -> ApiErrorUi(Res.string.err_invalid_match_qr_title, Res.string.err_invalid_match_qr_desc, SnackbarVariant.Error)
    "SELF_MATCH_INVITE" -> ApiErrorUi(Res.string.err_self_match_invite_title, Res.string.err_self_match_invite_desc, SnackbarVariant.Warn)
    "PARTICIPANT_NOT_AVAILABLE" -> ApiErrorUi(Res.string.err_participant_not_available_title, Res.string.err_participant_not_available_desc, SnackbarVariant.Warn)
    "MATCH_INVITE_NOT_FOUND" -> ApiErrorUi(Res.string.err_match_invite_not_found_title, Res.string.err_match_invite_not_found_desc, SnackbarVariant.Error)
    "MATCH_INVITE_NOT_PENDING" -> ApiErrorUi(Res.string.err_match_invite_not_pending_title, Res.string.err_match_invite_not_pending_desc, SnackbarVariant.Warn)
    "DUPLICATE_MATCH_INVITE" -> ApiErrorUi(Res.string.err_duplicate_match_invite_title, Res.string.err_duplicate_match_invite_desc, SnackbarVariant.Warn)
    "MATCH_NOT_FOUND" -> ApiErrorUi(Res.string.err_match_not_found_title, Res.string.err_match_not_found_desc, SnackbarVariant.Error)
    "MATCH_NOT_IN_READY_WAITING" -> ApiErrorUi(Res.string.err_match_not_ready_waiting_title, Res.string.err_match_not_ready_waiting_desc, SnackbarVariant.Warn)
    "MATCH_NOT_IN_RESULT_PHASE" -> ApiErrorUi(Res.string.err_match_not_result_phase_title, Res.string.err_match_not_result_phase_desc, SnackbarVariant.Warn)
    "MATCH_NOT_CANCELLABLE" -> ApiErrorUi(Res.string.err_match_not_cancellable_title, Res.string.err_match_not_cancellable_desc, SnackbarVariant.Warn)
    "CONFLICTING_RESULT" -> ApiErrorUi(Res.string.err_conflicting_result_title, Res.string.err_conflicting_result_desc, SnackbarVariant.Warn)
    "PARTICIPANT_NOT_FOUND" -> ApiErrorUi(Res.string.err_participant_not_found_title, Res.string.err_participant_not_found_desc, SnackbarVariant.Error)
    "MATCH_NOT_IN_TASK_PICK" -> ApiErrorUi(Res.string.err_match_not_task_pick_title, Res.string.err_match_not_task_pick_desc, SnackbarVariant.Warn)
    "MATCH_NOT_IN_TASK_OFFER" -> ApiErrorUi(Res.string.err_match_not_task_offer_title, Res.string.err_match_not_task_offer_desc, SnackbarVariant.Warn)
    "MATCH_NOT_IN_TASK_ACTIVE" -> ApiErrorUi(Res.string.err_match_not_task_active_title, Res.string.err_match_not_task_active_desc, SnackbarVariant.Warn)
    "MATCH_NOT_COMPLETED" -> ApiErrorUi(Res.string.err_match_not_completed_title, Res.string.err_match_not_completed_desc, SnackbarVariant.Warn)
    "TASK_NOT_FOUND" -> ApiErrorUi(Res.string.err_task_not_found_title, Res.string.err_task_not_found_desc, SnackbarVariant.Error)
    "TASK_NOT_ALLOWED_IN_PHASE" -> ApiErrorUi(Res.string.err_task_not_allowed_phase_title, Res.string.err_task_not_allowed_phase_desc, SnackbarVariant.Warn)

    else -> null
}

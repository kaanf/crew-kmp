package com.kaanf.game.presentation.session.coachmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.coachmark.CoachmarkStep
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.coachmark_intro_body
import crew.feature.game.presentation.generated.resources.coachmark_intro_title
import crew.feature.game.presentation.generated.resources.coachmark_qr_body
import crew.feature.game.presentation.generated.resources.coachmark_qr_title
import crew.feature.game.presentation.generated.resources.coachmark_scan_body
import crew.feature.game.presentation.generated.resources.coachmark_scan_title
import crew.feature.game.presentation.generated.resources.coachmark_score_body
import crew.feature.game.presentation.generated.resources.coachmark_score_highlight
import crew.feature.game.presentation.generated.resources.coachmark_score_title
import crew.feature.game.presentation.generated.resources.coachmark_tabs_body
import crew.feature.game.presentation.generated.resources.coachmark_tabs_title
import org.jetbrains.compose.resources.stringResource

/** QR home coachmark hedefleri; [CoachmarkStep.key] olarak kullanılır. */
enum class GameCoachmarkKey { Qr, Scan, Score, Tabs }

/** Tasarımdaki HOME_COACH_STEPS'in karşılığı: intro + 4 hedefli adım. */
@Composable
fun rememberGameCoachmarkSteps(): List<CoachmarkStep> {
    val steps = listOf(
        CoachmarkStep(
            title = stringResource(Res.string.coachmark_intro_title),
            body = stringResource(Res.string.coachmark_intro_body),
        ),
        CoachmarkStep(
            key = GameCoachmarkKey.Qr,
            title = stringResource(Res.string.coachmark_qr_title),
            body = stringResource(Res.string.coachmark_qr_body),
            cornerRadius = 20.dp,
            padding = 10.dp,
        ),
        CoachmarkStep(
            key = GameCoachmarkKey.Scan,
            title = stringResource(Res.string.coachmark_scan_title),
            body = stringResource(Res.string.coachmark_scan_body),
            cornerRadius = 999.dp,
        ),
        CoachmarkStep(
            key = GameCoachmarkKey.Score,
            title = stringResource(Res.string.coachmark_score_title),
            body = stringResource(Res.string.coachmark_score_body),
            highlight = stringResource(Res.string.coachmark_score_highlight),
            cornerRadius = 12.dp,
        ),
        CoachmarkStep(
            key = GameCoachmarkKey.Tabs,
            title = stringResource(Res.string.coachmark_tabs_title),
            body = stringResource(Res.string.coachmark_tabs_body),
            cornerRadius = 22.dp,
            padding = 8.dp,
        ),
    )
    // Kimliği sabitle: içerik değişmedikçe aynı liste instance'ı dönsün ki
    // CoachmarkHost'taki remember(steps) her recomposition'da state'i sıfırlamasın.
    return remember(steps) { steps }
}

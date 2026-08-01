package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.header.SectionHeader
import com.kaanf.core.designsystem.component.progressbar.WaitingIndicatorRow
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_loser_waits_description
import crew.feature.game.presentation.generated.resources.match_phase_loser_waits_eyebrow
import crew.feature.game.presentation.generated.resources.match_phase_loser_waits_status
import crew.feature.game.presentation.generated.resources.match_phase_loser_waits_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoserWaitsPhase(
    opponentName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionHeader(
            eyebrow = stringResource(Res.string.match_phase_loser_waits_eyebrow),
            title = stringResource(Res.string.match_phase_loser_waits_title, opponentName),
            description = stringResource(
                Res.string.match_phase_loser_waits_description,
                opponentName,
            ),
            verticalSpacing = 12.dp,
        )

        Spacer(modifier = Modifier.width(1.dp))

        WaitingIndicatorRow(text = stringResource(Res.string.match_phase_loser_waits_status))
    }
}

@Preview
@Composable
fun LoserWaitsPhasePreview() {
    CrewTheme { LoserWaitsPhase("Kaan") }
}

package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.game.presentation.component.OnboardingInfoCard
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionState
import com.kaanf.game.presentation.session.component.LostThrowInfoCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_description
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_scan_action
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_title_highlight
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_title_prefix
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun QrHomePhase(
    state: MatchSessionState,
    onAction: (MatchSessionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()
    val titlePrefix = stringResource(Res.string.match_phase_qr_home_title_prefix)
    val titleHighlight = stringResource(Res.string.match_phase_qr_home_title_highlight)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = AccessDefaults.TextPrimary)) {
                    append(titlePrefix)
                }
                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(titleHighlight)
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.match_phase_qr_home_description),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(1.dp))

        UserQrCard(
            modifier = Modifier.size(300.dp),
            inputText = state.matchQrToken.orEmpty(),
        )

        Spacer(modifier = Modifier.height(1.dp))

        BaseButton(
            text = stringResource(Res.string.match_phase_qr_home_scan_action),
            backgroundColor = AccessDefaults.Surface,
            onClick = {
                scope.launch {
                    val result = permissionController.requestPermission(Permission.CAMERA)
                    if (result == PermissionState.GRANTED) {
                        onAction(MatchSessionAction.OnScanClicked)
                    }
                }
            },
            filled = false,
            contentColor = AccessDefaults.TextPrimary,
            leadingIcon = AccessIcons.QR,
        )

        Spacer(modifier = Modifier.height(1.dp))

        OnboardingInfoCard()

        LostThrowInfoCard()
    }
}

package com.kaanf.auth.presentation.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.auth.domain.model.SocialProvider
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.util.ObserveAsEvents
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.social_continue_apple
import crew.feature.auth.presentation.generated.resources.social_continue_google
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Marka kuralları: koyu zeminde Apple beyaz buton ister (outline'lı stil koyu zeminde
// önerilmiyor), Google'ın kenarlıksız "Neutral" varyantı #F2F2F2'dir — beyaz varyantın
// #747775 stroke'u açık zeminde ayrışmak içindir, bize gereksiz.
// Google "G" logosu asla boyanmaz (tintLeadingIcon = false), Apple glifi içerik rengiyle boyanır.
private val AppleButtonBackground = Color.White
private val GoogleButtonBackground = Color(0xFFF2F2F2)
private val BrandButtonContent = Color(0xFF1F1F1F)

@Composable
fun SocialAuthSection(
    onLoginSuccess: () -> Unit,
    onProfileIncomplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SocialLoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher =
        rememberSocialSignInLauncher { result ->
            viewModel.onAction(SocialLoginAction.OnSignInResult(result))
        }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SocialLoginEvent.LaunchProvider -> launcher.launch(event.provider)
            SocialLoginEvent.NavigateToDashboard -> onLoginSuccess()
            SocialLoginEvent.NavigateToProfilePicture -> onProfileIncomplete()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BaseButton(
            text = stringResource(Res.string.social_continue_google),
            onClick = { viewModel.onAction(SocialLoginAction.OnProviderClick(SocialProvider.Google)) },
            enabled = state.pendingProvider == null && !state.isSubmitting,
            isLoading = state.pendingProvider == SocialProvider.Google,
            leadingIcon = AccessIcons.GoogleG,
            tintLeadingIcon = false,
            backgroundColor = GoogleButtonBackground,
            borderColor = GoogleButtonBackground,
            contentColor = BrandButtonContent,
        )

        BaseButton(
            text = stringResource(Res.string.social_continue_apple),
            onClick = { viewModel.onAction(SocialLoginAction.OnProviderClick(SocialProvider.Apple)) },
            enabled = state.pendingProvider == null && !state.isSubmitting,
            isLoading = state.pendingProvider == SocialProvider.Apple,
            leadingIcon = AccessIcons.AppleLogo,
            backgroundColor = AppleButtonBackground,
            borderColor = AppleButtonBackground,
            contentColor = BrandButtonContent,
        )
    }

    if (state.consentProvider != null) {
        ConsentBottomSheet(
            onDismiss = { viewModel.onAction(SocialLoginAction.OnConsentDismiss) },
            onConfirm = { viewModel.onAction(SocialLoginAction.OnConsentConfirm) },
        )
    }
}

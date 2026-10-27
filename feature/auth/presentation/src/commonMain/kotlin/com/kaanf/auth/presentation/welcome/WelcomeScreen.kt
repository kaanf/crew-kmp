package com.kaanf.auth.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.presentation.util.ReportFullyDrawnWhen
import com.kaanf.auth.presentation.onboarding.OnboardingScreen
import com.kaanf.auth.presentation.onboarding.OnboardingViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.auth.presentation.social.SocialAuthSection
import com.kaanf.auth.presentation.util.PolicyUrls
import com.kaanf.auth.presentation.util.appendPolicyLink
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.TestTags
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.welcome_body
import crew.feature.auth.presentation.generated.resources.welcome_headline_strangers
import crew.feature.auth.presentation.generated.resources.welcome_headline_until
import crew.feature.auth.presentation.generated.resources.welcome_headline_game
import crew.feature.auth.presentation.generated.resources.welcome_house_rules
import crew.feature.auth.presentation.generated.resources.welcome_continue_with_email
import crew.feature.auth.presentation.generated.resources.welcome_privacy_policy
import crew.feature.auth.presentation.generated.resources.social_continue_google
import crew.feature.auth.presentation.generated.resources.social_continue_apple
import crew.feature.auth.presentation.generated.resources.social_divider
import crew.feature.auth.presentation.generated.resources.welcome_terms
import crew.feature.auth.presentation.generated.resources.welcome_terms_conjunction
import crew.feature.auth.presentation.generated.resources.welcome_terms_middle
import crew.feature.auth.presentation.generated.resources.welcome_terms_prefix
import crew.feature.auth.presentation.generated.resources.welcome_terms_suffix
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.layout.ContentScale
import crew.feature.auth.presentation.generated.resources.welcome_connected_friends
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeRoot(
    onContinueWithEmailClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onProfileIncomplete: () -> Unit,
) {
    val onboardingViewModel: OnboardingViewModel = koinViewModel()
    val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()

    // Oturum açılmamış cold start burada biter: completed null olduğu sürece ekran hâlâ
    // boş bir zemin, yani kullanıcı için splash'ın devamı.
    ReportFullyDrawnWhen { onboardingState.completed != null }

    when (onboardingState.completed) {
        null -> {
            Box(Modifier.fillMaxSize().background(AccessDefaults.Background))
            return
        }
        false -> {
            OnboardingScreen(
                onComplete = onboardingViewModel::complete,
                isSaving = onboardingState.isSaving,
                hasError = onboardingState.hasError,
            )
            return
        }
        true -> Unit
    }
    WelcomeScreen(
        onContinueWithEmailClick = onContinueWithEmailClick,
        socialAuthSection = {
            SocialAuthSection(
                onLoginSuccess = onLoginSuccess,
                onProfileIncomplete = onProfileIncomplete,
            )
        },
    )
}

@Composable
fun WelcomeScreen(
    onContinueWithEmailClick: () -> Unit,
    modifier: Modifier = Modifier,
    socialAuthSection: @Composable () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.WELCOME_SCREEN)
            .background(AccessDefaults.Background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val viewportHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = viewportHeight)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            Content(artworkHeight = if (viewportHeight < 680.dp) 120.dp else 180.dp)
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.weight(1f))
            Footer(
                onContinueWithEmailClick = onContinueWithEmailClick,
                socialAuthSection = socialAuthSection,
            )
        }
    }
}

@Composable
private fun Content(artworkHeight: Dp) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.welcome_connected_friends),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth().height(artworkHeight),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text =
                buildAnnotatedString {
                    append(stringResource(Res.string.welcome_headline_strangers))
                    append("\n")
                    append(stringResource(Res.string.welcome_headline_until))
                    append(" ")
                    withStyle(SpanStyle(color = AccessDefaults.Accent)) {
                        append(stringResource(Res.string.welcome_headline_game))
                    }
                },
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.welcome_body),
            modifier =
                Modifier
                    .padding(top = 14.dp),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = AccessDefaults.TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun Footer(
    onContinueWithEmailClick: () -> Unit,
    socialAuthSection: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        socialAuthSection()

        Text(
            text = stringResource(Res.string.social_divider),
            modifier = Modifier.padding(vertical = 2.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AccessDefaults.TextMuted,
            ),
        )

        BaseButton(
            text = stringResource(Res.string.welcome_continue_with_email),
            onClick = onContinueWithEmailClick,
            modifier = Modifier.testTag(TestTags.WELCOME_LOGIN),
            filled = false,
            leadingIcon = AccessIcons.Mail,
        )

        Text(
            text =
                buildAnnotatedString {
                    append(stringResource(Res.string.welcome_terms_prefix))
                    appendPolicyLink(
                        text = stringResource(Res.string.welcome_terms),
                        url = PolicyUrls.TERMS_OF_USE,
                    )
                    append(stringResource(Res.string.welcome_terms_middle))
                    appendPolicyLink(
                        text = stringResource(Res.string.welcome_house_rules),
                        url = PolicyUrls.HOUSE_RULES,
                    )
                    append(stringResource(Res.string.welcome_terms_conjunction))
                    appendPolicyLink(
                        text = stringResource(Res.string.welcome_privacy_policy),
                        url = PolicyUrls.PRIVACY_POLICY,
                    )
                    append(stringResource(Res.string.welcome_terms_suffix))
                },
            modifier =
                Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                color = AccessDefaults.TextMuted,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    CrewTheme(isDarkTheme = true) {
        WelcomeScreen(
            onContinueWithEmailClick = {},
            socialAuthSection = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BaseButton(
                        text = stringResource(Res.string.social_continue_google),
                        onClick = {},
                        leadingIcon = AccessIcons.GoogleG,
                        tintLeadingIcon = false,
                        backgroundColor = Color(0xFFF2F2F2),
                        borderColor = Color(0xFFF2F2F2),
                        contentColor = Color(0xFF1F1F1F),
                    )
                    BaseButton(
                        text = stringResource(Res.string.social_continue_apple),
                        onClick = {},
                        leadingIcon = AccessIcons.AppleLogo,
                        backgroundColor = Color.White,
                        borderColor = Color.White,
                        contentColor = Color(0xFF1F1F1F),
                    )
                }
            },
        )
    }
}

package com.kaanf.auth.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import crew.feature.auth.presentation.generated.resources.welcome_headline_leave_with
import crew.feature.auth.presentation.generated.resources.welcome_headline_show_up
import crew.feature.auth.presentation.generated.resources.welcome_headline_solo
import crew.feature.auth.presentation.generated.resources.welcome_headline_story
import crew.feature.auth.presentation.generated.resources.welcome_house_rules
import crew.feature.auth.presentation.generated.resources.welcome_continue_with_email
import crew.feature.auth.presentation.generated.resources.welcome_privacy_policy
import crew.feature.auth.presentation.generated.resources.social_divider
import crew.feature.auth.presentation.generated.resources.welcome_terms
import crew.feature.auth.presentation.generated.resources.welcome_terms_conjunction
import crew.feature.auth.presentation.generated.resources.welcome_terms_middle
import crew.feature.auth.presentation.generated.resources.welcome_terms_prefix
import crew.feature.auth.presentation.generated.resources.welcome_terms_suffix
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeRoot(
    onContinueWithEmailClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onProfileIncomplete: () -> Unit,
) {
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
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .testTag(TestTags.WELCOME_SCREEN)
                .background(AccessDefaults.Background)
                .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, end = 22.dp, bottom = 24.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Content()

                Spacer(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(24.dp),
                )

                Footer(
                    onContinueWithEmailClick = onContinueWithEmailClick,
                    socialAuthSection = socialAuthSection,
                )
            }
        }
    }
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text =
                buildAnnotatedString {
                    append(stringResource(Res.string.welcome_headline_show_up))
                    append("\n")
                    withStyle(
                        SpanStyle(
                            color = AccessDefaults.Accent,
                            shadow = Shadow(
                                color = AccessDefaults.AccentGlow,
                                offset = Offset.Zero,
                                blurRadius = 24f,
                            ),
                        ),
                    ) {
                        append(stringResource(Res.string.welcome_headline_solo))
                    }
                    append("\n")
                    append(stringResource(Res.string.welcome_headline_leave_with))
                    append("\n")
                    append(stringResource(Res.string.welcome_headline_story))
                },
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = stringResource(Res.string.welcome_body),
            modifier =
                Modifier
                    .padding(top = 14.dp),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
                color = AccessDefaults.TextSecondary
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
            filled = true,
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
        )
    }
}

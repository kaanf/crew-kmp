package com.kaanf.auth.presentation.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaanf.auth.presentation.util.PolicyUrls
import com.kaanf.auth.presentation.util.appendPolicyLink
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.checkbox.BaseCheckbox
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.consent_sheet_continue
import crew.feature.auth.presentation.generated.resources.consent_sheet_title
import crew.feature.auth.presentation.generated.resources.register_age_confirmation
import crew.feature.auth.presentation.generated.resources.register_house_rules
import crew.feature.auth.presentation.generated.resources.register_privacy_policy
import crew.feature.auth.presentation.generated.resources.register_terms
import crew.feature.auth.presentation.generated.resources.register_terms_conjunction
import crew.feature.auth.presentation.generated.resources.register_terms_prefix
import crew.feature.auth.presentation.generated.resources.register_terms_separator
import crew.feature.auth.presentation.generated.resources.register_terms_suffix
import org.jetbrains.compose.resources.stringResource

/**
 * Sosyal girişte, provider akışı tetiklenmeden ÖNCE gösterilen onay sheet'i (PRD §5).
 * Kapatmak = vazgeçmek; hesap oluşturulmaz. İki kutu da işaretlenmeden devam edilemez.
 */
@Composable
fun ConsentBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var ageConfirmed by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    ContainerBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.consent_sheet_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = AccessDefaults.TextPrimary,
                    textAlign = TextAlign.Start,
                ),
            )

            BaseCheckbox(
                checked = ageConfirmed,
                onCheckedChange = { ageConfirmed = it },
                label = stringResource(Res.string.register_age_confirmation),
            )

            BaseCheckbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                label =
                    buildAnnotatedString {
                        append(stringResource(Res.string.register_terms_prefix))
                        appendPolicyLink(
                            text = stringResource(Res.string.register_terms),
                            url = PolicyUrls.TERMS_OF_USE,
                        )
                        append(stringResource(Res.string.register_terms_separator))
                        appendPolicyLink(
                            text = stringResource(Res.string.register_house_rules),
                            url = PolicyUrls.HOUSE_RULES,
                        )
                        append(stringResource(Res.string.register_terms_conjunction))
                        appendPolicyLink(
                            text = stringResource(Res.string.register_privacy_policy),
                            url = PolicyUrls.PRIVACY_POLICY,
                        )
                        append(stringResource(Res.string.register_terms_suffix))
                    },
            )

            BaseButton(
                text = stringResource(Res.string.consent_sheet_continue),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                enabled = ageConfirmed && termsAccepted,
                filled = true,
            )
        }
    }
}

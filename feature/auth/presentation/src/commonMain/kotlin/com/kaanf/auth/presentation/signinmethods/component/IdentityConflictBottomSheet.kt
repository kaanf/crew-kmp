package com.kaanf.auth.presentation.signinmethods.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.sign_in_methods_conflict_confirm
import crew.feature.auth.presentation.generated.resources.sign_in_methods_conflict_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_conflict_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Sağlayıcı hesabı başka bir Crew profiline bağlı. Snackbar yerine sheet: kullanıcının
 * bilmesi gereken tek şey iki geçmişin kendiliğinden birleşmeyeceği ve tek çıkış yolunun
 * o hesapla giriş yapmak olduğu. Tek aksiyon var, o yüzden tek buton.
 */
@Composable
fun IdentityConflictBottomSheet(
    providerName: String,
    onDismiss: () -> Unit,
) {
    ContainerBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .background(AccessDefaults.DangerBackground, CircleShape)
                    .border(1.dp, AccessDefaults.DangerBorder, CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Info),
                    tint = AccessDefaults.Rose,
                    contentDescription = null,
                )
            }

            Text(
                text = stringResource(Res.string.sign_in_methods_conflict_title, providerName),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    textAlign = TextAlign.Center,
                ),
            )

            Text(
                text = stringResource(Res.string.sign_in_methods_conflict_description),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                ),
            )

            BaseButton(
                text = stringResource(Res.string.sign_in_methods_conflict_confirm),
                onClick = onDismiss,
                filled = true,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

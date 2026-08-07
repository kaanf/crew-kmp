package com.kaanf.auth.presentation.signinmethods.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.sign_in_methods_connect
import crew.feature.auth.presentation.generated.resources.sign_in_methods_locked
import crew.feature.auth.presentation.generated.resources.sign_in_methods_sign_up_chip
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlink
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Satırın sağındaki tek aksiyon; hangisinin gösterileceğini ekran belirler. */
sealed interface SignInMethodAction {
    data class Connect(val onClick: () -> Unit) : SignInMethodAction
    data class Unlink(val onClick: () -> Unit) : SignInMethodAction

    /** Hesabın son giriş yolu: kaldırılamaz. */
    data object Locked : SignInMethodAction
    data object Busy : SignInMethodAction

    /** Satırın sağında aksiyon yok (e-posta satırı: şifre bağlantısı gövdede duruyor). */
    data object None : SignInMethodAction
}

@Composable
fun SignInMethodRow(
    icon: DrawableResource,
    name: String,
    subtitle: String,
    isLinked: Boolean,
    action: SignInMethodAction,
    modifier: Modifier = Modifier,
    // Google "G" gibi çok renkli marka logoları boyanmaz.
    tintIcon: Boolean = true,
    iconBackground: Color = AccessDefaults.SurfaceElevated,
    iconTint: Color = AccessDefaults.TextPrimary,
    isSignUpMethod: Boolean = false,
    footerLabel: String? = null,
    onFooterClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isLinked) AccessDefaults.SurfaceElevated else AccessDefaults.Surface,
                shape = RoundedCornerShape(16.dp),
            )
            .border(1.dp, AccessDefaults.BorderSoft, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        MethodGlyph(
            icon = icon,
            tintIcon = tintIcon,
            background = iconBackground,
            tint = iconTint,
            showLinkedPip = isLinked,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    ),
                )

                if (isSignUpMethod) {
                    SignUpChip()
                }
            }

            Text(
                text = subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isLinked) AccessDefaults.TextSecondary else AccessDefaults.TextFaint,
                    fontSize = 12.sp,
                ),
            )

            if (footerLabel != null && onFooterClick != null) {
                Text(
                    text = footerLabel,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onFooterClick,
                        ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.Sky,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp,
                    ),
                )
            }
        }

        RowAction(action = action)
    }
}

@Composable
private fun MethodGlyph(
    icon: DrawableResource,
    tintIcon: Boolean,
    background: Color,
    tint: Color,
    showLinkedPip: Boolean,
) {
    Box(modifier = Modifier.size(42.dp)) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(background, RoundedCornerShape(11.dp))
                .border(1.dp, AccessDefaults.BorderSoft, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(19.dp),
                painter = painterResource(icon),
                tint = if (tintIcon) tint else Color.Unspecified,
                contentDescription = null,
            )
        }

        if (showLinkedPip) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(AccessDefaults.Accent)
                    .border(2.dp, AccessDefaults.SurfaceElevated, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(9.dp),
                    painter = painterResource(AccessIcons.Check),
                    tint = AccessDefaults.OnAccent,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun SignUpChip() {
    Text(
        text = stringResource(Res.string.sign_in_methods_sign_up_chip),
        modifier = Modifier
            .background(
                color = AccessDefaults.Accent.copy(alpha = 0.15f),
                shape = CircleShape,
            )
            .border(1.dp, AccessDefaults.Accent.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium.copy(
            color = AccessDefaults.Accent,
            fontWeight = FontWeight.Bold,
            fontSize = 8.5.sp,
            letterSpacing = 1.sp,
        ),
    )
}

@Composable
private fun RowAction(action: SignInMethodAction) {
    when (action) {
        is SignInMethodAction.Connect ->
            ActionPill(
                label = stringResource(Res.string.sign_in_methods_connect),
                background = AccessDefaults.Accent,
                contentColor = AccessDefaults.OnAccent,
                borderColor = AccessDefaults.Accent,
                onClick = action.onClick,
            )

        is SignInMethodAction.Unlink ->
            ActionPill(
                label = stringResource(Res.string.sign_in_methods_unlink),
                background = AccessDefaults.SurfaceHigh,
                contentColor = AccessDefaults.TextSecondary,
                borderColor = AccessDefaults.Border,
                onClick = action.onClick,
            )

        SignInMethodAction.Locked ->
            ActionPill(
                label = stringResource(Res.string.sign_in_methods_locked),
                background = Color.Transparent,
                contentColor = AccessDefaults.TextFaint,
                borderColor = AccessDefaults.Border,
                onClick = null,
            )

        SignInMethodAction.None -> Unit

        SignInMethodAction.Busy ->
            Box(
                modifier = Modifier.height(32.dp).size(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = AccessDefaults.Accent,
                    strokeWidth = 2.dp,
                )
            }
    }
}

@Composable
private fun ActionPill(
    label: String,
    background: Color,
    contentColor: Color,
    borderColor: Color,
    onClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, borderColor, CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
        )
    }
}

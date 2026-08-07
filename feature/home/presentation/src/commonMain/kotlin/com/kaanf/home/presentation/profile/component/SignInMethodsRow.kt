package com.kaanf.home.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Kilitli hesap detaylarının altındaki tek eyleme çağıran kart: giriş yöntemleri ekranına götürür.
 *
 * Tasarımdaki alt satır hangi yöntemle girildiğini yazıyor; profil o veriyi taşımıyor (ayrı bir
 * uç) ve bunun için ayrıca istek atmaya değmez — sabit bir açıklama aynı işi görüyor.
 */
@Composable
fun SignInMethodsRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AccessDefaults.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, AccessDefaults.BorderSoft, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = AccessDefaults.Accent.copy(alpha = 0.13f),
                    shape = RoundedCornerShape(10.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(17.dp),
                painter = painterResource(AccessIcons.Link),
                tint = AccessDefaults.Accent,
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "Sign-in methods",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                ),
            )

            Text(
                text = "Apple, Google or a password — all one account",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 11.5.sp,
                ),
            )
        }

        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(AccessIcons.RightChevron),
            tint = AccessDefaults.TextFaint,
            contentDescription = null,
        )
    }
}

@Composable
@Preview
private fun SignInMethodsRowPreview() {
    CrewTheme {
        Box(modifier = Modifier.background(AccessDefaults.Background).padding(16.dp)) {
            SignInMethodsRow(onClick = {})
        }
    }
}

package com.kaanf.game.presentation.passport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.passport_rare_status_collected
import crew.feature.game.presentation.generated.resources.passport_rare_status_missing
import org.jetbrains.compose.resources.stringResource

/** Nadir damga listesi satırı: rozet + nasıl kazanılacağı + sende/eksik durumu. */
@Composable
fun PassportRareStampRow(
    emoji: String,
    name: String,
    hint: String,
    isCollected: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(30.dp)
                .alpha(if (isCollected) 1f else 0.4f),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 11.sp,
                ),
            )
        }
        Text(
            text = stringResource(
                if (isCollected) {
                    Res.string.passport_rare_status_collected
                } else {
                    Res.string.passport_rare_status_missing
                },
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isCollected) AccessDefaults.Amber else AccessDefaults.TextMuted,
            ),
            modifier = Modifier
                .background(
                    color = if (isCollected) {
                        AccessDefaults.Amber.copy(alpha = 0.16f)
                    } else {
                        AccessDefaults.SurfaceElevated
                    },
                    shape = AccessShapes.Pill,
                )
                .padding(horizontal = 9.dp, vertical = 4.dp),
        )
    }
}

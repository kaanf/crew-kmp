package com.kaanf.game.presentation.passport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.game.presentation.passport.PassportStampUi
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.passport_source_rps_lost
import crew.feature.game.presentation.generated.resources.passport_source_rps_won
import crew.feature.game.presentation.generated.resources.passport_source_task_format
import org.jetbrains.compose.resources.stringResource

/** Seçili damganın kim olduğu ve nasıl toplandığı. */
@Composable
fun PassportStampDetailCard(
    stamp: PassportStampUi,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = AccessDefaults.Surface, shape = shape)
            .border(width = 1.dp, color = AccessDefaults.Border, shape = shape)
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PassportStamp(
            stamp = stamp,
            initialTextSize = 16.sp,
            modifier = Modifier.width(40.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stamp.ownerName,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            val source = stamp.firstMatchTaskTitle
                ?.let { stringResource(Res.string.passport_source_task_format, it) }
                ?: stringResource(
                    if (stamp.firstMatchWon) {
                        Res.string.passport_source_rps_won
                    } else {
                        Res.string.passport_source_rps_lost
                    },
                )
            Text(
                text = "${stamp.collectedAt} · $source",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                ),
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

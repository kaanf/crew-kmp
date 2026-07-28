package com.kaanf.game.presentation.passport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.game.presentation.passport.PassportStampUi
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.passport_filled_format
import crew.feature.game.presentation.generated.resources.passport_rare_count_format
import org.jetbrains.compose.resources.stringResource

private const val COLUMNS = 4

/**
 * Pasaportun bir sayfası: damga gridi + sayfa başlığı + doluluk satırı.
 * Boş kutular kasıtlı olarak görünür — "kim eksik?" hissi tasarımın parçası.
 */
@Composable
fun PassportPageCard(
    stamps: List<PassportStampUi>,
    totalSlots: Int,
    headerLeft: String,
    headerRight: String,
    selectedStampId: String?,
    onStampClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = AccessShapes.XLarge
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AccessDefaults.SurfaceElevated, AccessDefaults.Surface),
                ),
                shape = shape,
            )
            .border(width = 1.dp, color = AccessDefaults.Border, shape = shape)
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp, bottom = 13.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = headerLeft.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(color = AccessDefaults.TextMuted),
            )
            Text(
                text = headerRight.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(color = AccessDefaults.TextMuted),
            )
        }

        val cellCount = maxOf(totalSlots, stamps.size)
        Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (0 until cellCount).chunked(COLUMNS).forEach { rowIndices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowIndices.forEach { index ->
                        val stamp = stamps.getOrNull(index)
                        if (stamp != null) {
                            PassportStamp(
                                stamp = stamp,
                                isSelected = stamp.id == selectedStampId,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onStampClick(stamp.id) },
                            )
                        } else {
                            PassportEmptySlot(modifier = Modifier.weight(1f))
                        }
                    }
                    repeat(COLUMNS - rowIndices.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        val rareCount = stamps.count { it.isRare }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    Res.string.passport_filled_format,
                    stamps.size,
                    totalSlots,
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextSecondary,
                ),
            )
            Text(
                text = stringResource(Res.string.passport_rare_count_format, rareCount),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (rareCount > 0) AccessDefaults.Amber else AccessDefaults.TextMuted,
                ),
            )
        }
    }
}

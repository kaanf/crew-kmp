package com.kaanf.core.designsystem.component.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.ic_chevron_left_24
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BaseSelectField(
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    enabled: Boolean = true,
    testTag: String? = null,
) {
    LabeledFieldLayout(
        modifier = modifier,
        label = label,
        hint = hint,
    ) {
        BaseInputField(
            enabled = enabled,
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = enabled,
                        onClick = onClick,
                    )
                    .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = value ?: placeholder,
                        color = if (value == null) AccessDefaults.TextFaint else AccessDefaults.TextPrimary,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                Icon(
                    imageVector = vectorResource(Res.drawable.ic_chevron_left_24),
                    contentDescription = null,
                    tint = AccessDefaults.TextFaint,
                    modifier =
                        Modifier
                            .size(18.dp)
                            .rotate(-90f),
                )
            }
        }
    }
}

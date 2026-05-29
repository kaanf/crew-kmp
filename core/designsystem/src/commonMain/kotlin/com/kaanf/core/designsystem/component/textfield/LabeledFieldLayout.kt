package com.kaanf.core.designsystem.component.textfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults

/**
 * Wraps an input field with an optional label (and trailing action) above it and a hint below it.
 * Renders nothing extra around [field] when [label], [trailing], and [hint] are all null.
 */
@Composable
internal fun LabeledFieldLayout(
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    field: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (label != null || trailing != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                trailing?.invoke()
            }
        }

        field()

        if (hint != null) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

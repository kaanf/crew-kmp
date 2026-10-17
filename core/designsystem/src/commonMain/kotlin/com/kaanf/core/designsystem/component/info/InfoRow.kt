package com.kaanf.core.designsystem.component.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import org.jetbrains.compose.resources.painterResource

@Composable
fun InfoRow(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = painterResource(AccessIcons.Info),
            contentDescription = null,
            tint = AccessDefaults.TextSecondary,
            modifier = Modifier.padding(top = 2.dp).size(22.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = AccessDefaults.TextPrimary,
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = AccessDefaults.TextSecondary,
            )
        }
    }
}

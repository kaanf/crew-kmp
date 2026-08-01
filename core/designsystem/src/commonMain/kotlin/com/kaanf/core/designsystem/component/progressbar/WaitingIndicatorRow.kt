package com.kaanf.core.designsystem.component.progressbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Karşı tarafın hamlesi beklenirken gösterilen üç nokta + durum metni satırı. */
@Composable
fun WaitingIndicatorRow(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 6.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThreeDotsAnimatedCard(
            dotRadius = 2.dp,
            spacing = 4.dp,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 11.sp,
            ),
        )
    }
}

@Preview
@Composable
private fun WaitingIndicatorRowPreview() {
    CrewTheme {
        WaitingIndicatorRow(text = "Waiting for response")
    }
}

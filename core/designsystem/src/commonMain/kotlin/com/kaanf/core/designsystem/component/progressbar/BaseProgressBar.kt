package com.kaanf.core.designsystem.component.progressbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseProgressBar(
    modifier: Modifier = Modifier,
    capacity: Int,
) {
    val progress = capacity.coerceIn(0, 100) / 100f

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(4.dp)
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = progress,
                        range = 0f..1f,
                    )
                }
                .background(
                    color = AccessDefaults.SurfaceHigh,
                    shape = RoundedCornerShape(percent = 50),
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(
                        color = AccessDefaults.Accent,
                        shape = RoundedCornerShape(percent = 50),
                    ),
        )
    }
}

@Preview
@Composable
private fun BaseProgressBarPreview() {
    CrewTheme(isDarkTheme = true) {
        BaseProgressBar(capacity = 42)
    }
}

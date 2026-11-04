package com.kaanf.core.designsystem.component.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun DottedBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(AccessDefaults.Background)
            .drawWithCache {
                val spacing = 20.dp.toPx()
                val radius = 1.25.dp.toPx()
                onDrawBehind {
                    var y = spacing / 2
                    while (y < size.height) {
                        var x = spacing / 2
                        while (x < size.width) {
                            drawCircle(AccessDefaults.BorderSoft, radius, Offset(x, y))
                            x += spacing
                        }
                        y += spacing
                    }
                }
            },
    )
}

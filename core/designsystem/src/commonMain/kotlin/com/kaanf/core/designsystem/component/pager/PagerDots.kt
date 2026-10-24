package com.kaanf.core.designsystem.component.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun PagerDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = AccessDefaults.Accent,
    inactiveColor: Color = AccessDefaults.Border,
    dotSize: Dp = 8.dp,
    enabled: Boolean = true,
    onPageClick: ((Int) -> Unit)? = null,
    pageDescription: (Int) -> String = { "${it + 1} / $pageCount" },
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (onPageClick == null) 6.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val description = pageDescription(index)
            Box(
                modifier = if (onPageClick != null) {
                    Modifier.width(24.dp).height(48.dp)
                        .selectable(
                            selected = index == currentPage,
                            enabled = enabled,
                            role = Role.Tab,
                            onClick = { onPageClick(index) },
                        )
                        .semantics { contentDescription = description }
                } else {
                    Modifier
                },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.size(dotSize).background(
                        color = if (index == currentPage) activeColor else inactiveColor,
                        shape = CircleShape,
                    ),
                )
            }
        }
    }
}

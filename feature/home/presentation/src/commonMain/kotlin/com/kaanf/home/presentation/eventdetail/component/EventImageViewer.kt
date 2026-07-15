package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kaanf.core.designsystem.component.dialog.fullscreenDialogProperties
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun EventImageViewer(
    imageUrls: List<String>,
    initialPage: Int,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = fullscreenDialogProperties(),
    ) {
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { imageUrls.size },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                BaseImage(
                    imageUrl = imageUrls[page],
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .safeDrawingPadding()
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(AccessDefaults.SurfaceElevated)
                    .border(width = 1.dp, color = AccessDefaults.BorderSoft, shape = CircleShape)
                    .size(32.dp),
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Close),
                    contentDescription = null,
                    tint = AccessDefaults.TextPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            if (imageUrls.size > 1) {
                PagerDots(
                    pagerState = pagerState,
                    activeColor = Color.White,
                    inactiveColor = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .safeDrawingPadding()
                        .padding(bottom = 24.dp),
                )
            }
        }
    }
}

@Composable
internal fun PagerDots(
    pagerState: PagerState,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(pagerState.pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == pagerState.currentPage) activeColor else inactiveColor,
                    ),
            )
        }
    }
}

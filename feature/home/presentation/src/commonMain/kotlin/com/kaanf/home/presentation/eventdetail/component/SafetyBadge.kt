package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SafetyBadge() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Accent.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12.dp),
                color = AccessDefaults.Accent.copy(alpha = 0.18f)
            )
            .padding(
                all = 16.dp
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(AccessIcons.Shield),
                contentDescription = "Shield icon",
                tint = AccessDefaults.Accent,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "Safety First",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.Accent,
                    fontSize = 14.sp
                )
            )
        }

        Text(
            text = "A host is on-site the whole night. Block, report and skip-task buttons stay one tap away.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
@Preview
fun SafetyBadgePreview() {
    CrewTheme {
        Column(
            modifier = Modifier
                .background(AccessDefaults.Background)
                .padding(12.dp)
        ) { SafetyBadge() }
    }
}

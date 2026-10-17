package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SafetyBadge() {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccessDefaults.TextPrimary.copy(alpha = 0.04f))
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button) { expanded = !expanded }
                .semantics {
                    if (expanded) {
                        collapse { expanded = false; true }
                    } else {
                        expand { expanded = true; true }
                    }
                }
                .heightIn(min = 48.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(AccessIcons.Shield),
                contentDescription = null,
                tint = AccessDefaults.TextPrimary,
                modifier = Modifier.size(22.dp),
            )

            Text(
                text = "Safety First",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = BricolageGrotesque,
                    color = AccessDefaults.TextPrimary,
                    fontSize = 15.sp,
                ),
            )

            Icon(
                painter = painterResource(AccessIcons.RightChevron),
                contentDescription = null,
                tint = AccessDefaults.TextPrimary,
                modifier = Modifier.size(22.dp).rotate(if (expanded) 270f else 90f),
            )
        }

        if (expanded) {
            Text(
                text = "A host is on-site the whole night. Block, report and skip-task buttons stay one tap away.",
                modifier = Modifier.padding(start = 46.dp, end = 12.dp, bottom = 12.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

@Composable
@Preview
fun SafetyBadgePreview() {
    CrewTheme {
        Column(
            modifier = Modifier
                .background(AccessDefaults.Background)
                .padding(12.dp),
        ) { SafetyBadge() }
    }
}

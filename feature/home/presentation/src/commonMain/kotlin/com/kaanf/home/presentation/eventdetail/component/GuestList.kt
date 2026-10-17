package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.domain.model.EventGuest
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_detail_guest_list_arrived
import crew.feature.home.presentation.generated.resources.event_detail_guest_list_going
import crew.feature.home.presentation.generated.resources.event_detail_guest_list_title
import crew.feature.home.presentation.generated.resources.event_detail_guest_list_close
import crew.feature.home.presentation.generated.resources.event_detail_guest_list_locked
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LockedGuestListCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccessDefaults.TextPrimary.copy(alpha = 0.04f))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val silhouetteColor = AccessDefaults.TextPrimary.copy(alpha = 0.10f)
        Box(
            modifier = Modifier.size(width = 64.dp, height = 32.dp).clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val radius = 14.dp.toPx()
                repeat(3) { index ->
                    drawCircle(
                        color = silhouetteColor,
                        radius = radius,
                        center = Offset(radius + index * 18.dp.toPx(), size.height / 2),
                    )
                }
            }
            Icon(
                painter = painterResource(AccessIcons.Lock),
                contentDescription = null,
                tint = AccessDefaults.TextPrimary,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(Res.string.event_detail_guest_list_title),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = BricolageGrotesque,
                    fontSize = 15.sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            Text(
                text = stringResource(Res.string.event_detail_guest_list_locked),
                style = MaterialTheme.typography.bodySmall,
                color = AccessDefaults.TextSecondary,
            )
        }
    }
}

@Composable
fun GuestListCard(
    guests: List<EventGuest>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val previewGuests = guests.take(3)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccessDefaults.TextPrimary.copy(alpha = 0.04f))
            .clickable(enabled = !isLoading, role = Role.Button, onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(AccessIcons.User),
            contentDescription = null,
            tint = AccessDefaults.TextPrimary,
            modifier = Modifier.size(22.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(Res.string.event_detail_guest_list_title),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = BricolageGrotesque,
                    fontSize = 15.sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            Crossfade(
                targetState = isLoading,
                animationSpec = tween(durationMillis = 220),
                label = "Guest count loading",
            ) { loading ->
                // Reserve the subtitle's height without announcing a count while loading.
                Box {
                    Text(
                        text = stringResource(Res.string.event_detail_guest_list_going, guests.size),
                        modifier = if (loading) Modifier.clearAndSetSemantics {} else Modifier,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = if (loading) androidx.compose.ui.graphics.Color.Transparent
                            else AccessDefaults.TextSecondary,
                    )
                }
            }
        }
        Crossfade(
            targetState = isLoading,
            animationSpec = tween(durationMillis = 220),
            label = "Guest list loading",
        ) { loading ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = AccessDefaults.TextPrimary,
                        strokeWidth = 2.dp,
                    )
                } else if (previewGuests.isNotEmpty()) {
                    val extraCount = guests.size - previewGuests.size
                    val circleCount = previewGuests.size + if (extraCount > 0) 1 else 0
                    Box(
                        modifier = Modifier
                            .size(width = (28 + 28 / 1.5f * (circleCount - 1)).dp, height = 28.dp)
                            .clearAndSetSemantics {},
                    ) {
                        previewGuests.forEachIndexed { index, guest ->
                            AvatarCircle(
                                content = avatarContentFor(
                                    imageUrl = guest.profilePictureUrl,
                                    initialsLabel = guest.initial,
                                    seed = guest.userId,
                                ),
                                avatarSize = 28,
                                borderSize = 0,
                                modifier = Modifier.offset(x = (28 / 1.5f * index).dp),
                            )
                        }
                        if (extraCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (28 / 1.5f * previewGuests.size).dp)
                                    .size(28.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(AccessDefaults.SurfaceHigh),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "+$extraCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccessDefaults.TextSecondary,
                                )
                            }
                        }
                    }
                }
                if (!loading) Icon(
                    painter = painterResource(AccessIcons.RightChevron),
                    contentDescription = null,
                    tint = AccessDefaults.TextPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
fun GuestListSheet(
    guests: List<EventGuest>,
    onDismiss: () -> Unit,
) {
    ContainerBottomSheet(onDismiss = onDismiss) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringResource(Res.string.event_detail_guest_list_title),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = BricolageGrotesque,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = AccessDefaults.TextPrimary,
                )
                Text(
                    text = stringResource(Res.string.event_detail_guest_list_going, guests.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = AccessDefaults.TextSecondary,
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(AccessIcons.Close),
                    contentDescription = stringResource(Res.string.event_detail_guest_list_close),
                    tint = AccessDefaults.TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = AccessDefaults.TextPrimary.copy(alpha = 0.08f),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp),
        ) {
            items(items = guests, key = { it.userId }) { guest ->
                GuestRow(guest = guest)
            }
        }
    }
}

@Composable
private fun GuestRow(guest: EventGuest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarCircle(
            content = avatarContentFor(
                imageUrl = guest.profilePictureUrl,
                initialsLabel = guest.initial,
                seed = guest.userId,
            ),
            avatarSize = 44,
            borderSize = 0,
        )

        Text(
            text = guest.fullName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(color = AccessDefaults.TextPrimary),
        )

        if (guest.hasArrived) {
            Text(
                text = stringResource(Res.string.event_detail_guest_list_arrived),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AccessDefaults.Accent.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.Accent,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

private val EventGuest.initial: String
    get() = fullName.take(1).uppercase()

@Composable
@Preview
fun GuestListCardPreview() {
    CrewTheme {
        GuestListCard(
            guests = List(9) { index ->
                EventGuest(
                    userId = "user-$index",
                    fullName = "Guest $index",
                    profilePictureUrl = null,
                    hasArrived = index % 2 == 0,
                )
            },
            onClick = {},
        )
    }
}

package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LobbyFillingCard(
    modifier: Modifier = Modifier,
    currentCount: Int = 42,
    capacity: Int = 80,
    todayCount: Int = 9,
    avatars: List<LobbyAvatar> = listOf(
        LobbyAvatar("M", Color(0xFFC8FF3D)),
        LobbyAvatar("J", Color(0xFF6FB7FF)),
        LobbyAvatar("K", Color(0xFFFF7A5C)),
        LobbyAvatar("R", Color(0xFF5BE0C5)),
        LobbyAvatar("A", Color(0xFFFF5A7A)),
        LobbyAvatar("L", Color(0xFFFFB341)),
    )
) {
    val progress = currentCount / capacity.toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF16110D))
            .border(
                width = 1.dp,
                color = Color(0xFF2A241D),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "THE LOBBY'S FILLING UP",
                    color = Color(0xFFC8FF3D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$currentCount strangers in. You'd\nbe #${currentCount + 1}.",
                    color = Color(0xFFF5EFE6),
                    fontSize = 22.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            TodayBadge(count = todayCount)
        }

        AvatarStack(
            avatars = avatars,
            extraCount = 36
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StripedProgressBar(progress = progress)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFC8FF3D),
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) {
                            append("${capacity - currentCount - 36} more")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFBDB4A8),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(" and the night locks in")
                        }
                    },
                    fontSize = 16.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$currentCount",
                        color = Color(0xFFF5EFE6),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " / ",
                        color = Color(0xFF6E665D),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$capacity",
                        color = Color(0xFF8C8378),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatPill(
                highlight = "38%",
                text = "played before"
            )

            StatPill(
                leading = {
                    Text(
                        text = "★",
                        color = Color(0xFFFFB341),
                        fontSize = 18.sp
                    )
                },
                highlight = "4.7",
                text = "vibe"
            )

            StatPill(
                text = "2 you've met before"
            )
        }
    }
}

data class LobbyAvatar(
    val label: String,
    val color: Color
)

@Composable
private fun TodayBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF23300E))
            .border(
                width = 1.dp,
                color = Color(0xFF6E8F1D),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFFC8FF3D), CircleShape)
        )

        Text(
            text = "+$count today",
            color = Color(0xFFC8FF3D),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AvatarStack(
    avatars: List<LobbyAvatar>,
    extraCount: Int,
    modifier: Modifier = Modifier
) {
    val avatarSize = 46.dp
    val step = 34.dp
    val totalCount = avatars.size + 1

    Box(
        modifier = modifier
            .height(avatarSize)
            .width(avatarSize + step * (totalCount - 1))
    ) {
        avatars.forEachIndexed { index, avatar ->
            AvatarCircle(
                label = avatar.label,
                color = avatar.color,
                modifier = Modifier.offset(x = step * index)
            )
        }

        ExtraAvatarCircle(
            count = extraCount,
            modifier = Modifier.offset(x = step * avatars.size)
        )
    }
}

@Composable
private fun AvatarCircle(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color(0xFF14100C), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color(0xFF17110D),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ExtraAvatarCircle(
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF312B23))
            .border(2.dp, Color(0xFF14100C), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            color = Color(0xFFD3C9BA),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StripedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .clip(shape)
            .background(Color(0xFF282219))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(shape)
                .background(Color(0xFFC8FF3D))
                .drawWithContent {
                    drawContent()

                    val stripeGap = 18.dp.toPx()
                    val stroke = 2.dp.toPx()
                    var x = -size.height

                    while (x < size.width + size.height) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.18f),
                            start = Offset(x, size.height),
                            end = Offset(x + size.height, 0f),
                            strokeWidth = stroke
                        )
                        x += stripeGap
                    }
                }
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x55C8FF3D)
                        )
                    )
                )
        )
    }
}

@Composable
private fun StatPill(
    text: String,
    modifier: Modifier = Modifier,
    highlight: String? = null,
    leading: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF211B14))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        leading?.invoke()

        if (highlight != null) {
            Text(
                text = highlight,
                color = Color(0xFFC8FF3D),
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Text(
            text = text,
            color = Color(0xFFCFC5B7),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
@Preview
fun LobbyFillingCardPreview() {
    LobbyFillingCard()
}

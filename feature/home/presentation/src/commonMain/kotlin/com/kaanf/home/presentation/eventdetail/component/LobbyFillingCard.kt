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
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarStack
import com.kaanf.core.designsystem.component.avatar.ExtraAvatarCircle
import com.kaanf.core.presentation.model.UserAvatar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LobbyFillingCard(
    modifier: Modifier = Modifier,
    currentCount: Int = 42,
    capacity: Int = 80,
    todayCount: Int = 9,
    avatars: List<UserAvatar> = listOf(
        UserAvatar("M", Color(0xFFC8FF3D)),
        UserAvatar("J", Color(0xFF6FB7FF)),
        UserAvatar("K", Color(0xFFFF7A5C)),
        UserAvatar("R", Color(0xFF5BE0C5)),
        UserAvatar("A", Color(0xFFFF5A7A)),
        UserAvatar("L", Color(0xFFFFB341)),
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
        }

        AvatarStack(
            avatars = avatars,
            extraCount = 36
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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

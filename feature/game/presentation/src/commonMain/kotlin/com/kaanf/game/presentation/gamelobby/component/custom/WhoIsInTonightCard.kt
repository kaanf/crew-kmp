package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.avatar.AvatarStack
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.UserAvatar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WhoIsInTonightCard() {
    val avatars: List<UserAvatar> = listOf(
        UserAvatar("E", Color(0xFFFF5A7A)),
        UserAvatar("M", Color(0xFFC8FF3D)),
        UserAvatar("A", Color(0xFF5BE0C5)),
        UserAvatar("J", Color(0xFF6FB7FF)),
        UserAvatar("K", Color(0xFFFF7A5C)),
        UserAvatar("R", Color(0xFF5BE0C5)),
        UserAvatar("A", Color(0xFFFF5A7A)),
        UserAvatar("L", Color(0xFFFFB341)),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Card
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Card
            )
            .padding(all = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "WHO'S IN TONIGHT",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.Accent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                ),
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextPrimary,
                        ),
                    ) {
                        append(
                            "47 strangers,",
                        )
                    }

                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextMuted,
                        ),
                    ) {
                        append(
                            " one event."
                        )
                    }
                },
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 0.sp,
                ),
            )

            Spacer(modifier = Modifier.height(1.dp))

            AvatarStack(
                avatars = avatars,
                extraCount = 12,
                avatarSize = 32
            )

            Spacer(modifier = Modifier.height(1.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AccessDefaults.BorderSoft)
            )

            Spacer(modifier = Modifier.height(1.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarCircle(
                    content = AvatarContent.Initials(label = "KF", color = AccessDefaults.Rose),
                    avatarSize = 48,
                    textSize = 16.0,
                    borderColor = AccessDefaults.BorderSoft,
                    borderSize = 2,
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = AccessDefaults.TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                ),
                            ) {
                                append(
                                    "Kaan F.",
                                )
                            }

                            withStyle(
                                style = SpanStyle(
                                    color = AccessDefaults.TextMuted,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp
                                ),
                            ) {
                                append(
                                    "\tyour host"
                                )
                            }
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextPrimary,
                            fontSize = 15.sp
                        )
                    )

                    Text(
                        text = "Find the black shirt to say hi!",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                RoundedBadge(
                    text = "Host",
                    backgroundColor = AccessDefaults.SurfaceElevated,
                    borderColor = Color.Transparent,
                    textColor = AccessDefaults.TextSecondary,
                )
            }
        }
    }
}

@Composable
@Preview
fun WhoIsInTonightCardPreview() {
    CrewTheme {
        WhoIsInTonightCard()
    }
}

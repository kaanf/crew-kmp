package com.kaanf.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import crew.core.designsystem.generated.resources.BricolageGrotesque_Bold
import crew.core.designsystem.generated.resources.BricolageGrotesque_ExtraBold
import crew.core.designsystem.generated.resources.BricolageGrotesque_ExtraLight
import crew.core.designsystem.generated.resources.BricolageGrotesque_Light
import crew.core.designsystem.generated.resources.BricolageGrotesque_Medium
import crew.core.designsystem.generated.resources.BricolageGrotesque_Regular
import crew.core.designsystem.generated.resources.BricolageGrotesque_SemiBold
import crew.core.designsystem.generated.resources.JetBrainsMono_Bold
import crew.core.designsystem.generated.resources.JetBrainsMono_ExtraBold
import crew.core.designsystem.generated.resources.JetBrainsMono_Light
import crew.core.designsystem.generated.resources.JetBrainsMono_Medium
import crew.core.designsystem.generated.resources.JetBrainsMono_Regular
import crew.core.designsystem.generated.resources.JetBrainsMono_SemiBold
import crew.core.designsystem.generated.resources.JetBrainsMono_Thin
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.geist_medium
import crew.core.designsystem.generated.resources.geist_regular
import crew.core.designsystem.generated.resources.geist_semibold
import crew.core.designsystem.generated.resources.inter_variable
import crew.core.designsystem.generated.resources.special_elite_regular
import org.jetbrains.compose.resources.Font

val Geist
    @Composable get() =
        FontFamily(
            Font(
                resource = Res.font.geist_regular,
                weight = FontWeight.Normal,
            ),
            Font(
                resource = Res.font.geist_medium,
                weight = FontWeight.Medium,
            ),
            Font(
                resource = Res.font.geist_semibold,
                weight = FontWeight.SemiBold,
            ),
        )

val Inter
    @Composable get() =
        FontFamily(
            Font(
                resource = Res.font.inter_variable,
                weight = FontWeight.Light,
            ),
            Font(
                resource = Res.font.inter_variable,
                weight = FontWeight.Normal,
            ),
            Font(
                resource = Res.font.inter_variable,
                weight = FontWeight.Medium,
            ),
            Font(
                resource = Res.font.inter_variable,
                weight = FontWeight.SemiBold,
            ),
            Font(
                resource = Res.font.inter_variable,
                weight = FontWeight.Bold,
            ),
        )

val SpecialElite
    @Composable get() =
        FontFamily(
            Font(
                resource = Res.font.special_elite_regular,
                weight = FontWeight.Normal,
            ),
        )

val JetbrainsMono
    @Composable get() =
        FontFamily(
            Font(
                resource = Res.font.JetBrainsMono_Thin,
                weight = FontWeight.Thin,
            ),
            Font(
                resource = Res.font.JetBrainsMono_Light,
                weight = FontWeight.Light,
            ),
            Font(
                resource = Res.font.JetBrainsMono_Regular,
                weight = FontWeight.Normal,
            ),
            Font(
                resource = Res.font.JetBrainsMono_Medium,
                weight = FontWeight.Medium,
            ),
            Font(
                resource = Res.font.JetBrainsMono_SemiBold,
                weight = FontWeight.SemiBold,
            ),
            Font(
                resource = Res.font.JetBrainsMono_Bold,
                weight = FontWeight.Bold,
            ),
            Font(
                resource = Res.font.JetBrainsMono_ExtraBold,
                weight = FontWeight.ExtraBold,
            ),
        )

val BricolageGrotesque
    @Composable get() =
        FontFamily(
            Font(
                resource = Res.font.BricolageGrotesque_ExtraLight,
                weight = FontWeight.ExtraLight,
            ),
            Font(
                resource = Res.font.BricolageGrotesque_Light,
                weight = FontWeight.Light,
            ),
            Font(
                resource = Res.font.BricolageGrotesque_Regular,
                weight = FontWeight.Normal,
            ),
            Font(
                resource = Res.font.BricolageGrotesque_Medium,
                weight = FontWeight.Medium,
            ),
            Font(
                resource = Res.font.BricolageGrotesque_SemiBold,
                weight = FontWeight.SemiBold,
            ),
            Font(
                resource = Res.font.BricolageGrotesque_Bold,
                weight = FontWeight.Bold,
            ),
            Font(
                resource = Res.font.BricolageGrotesque_ExtraBold,
                weight = FontWeight.ExtraBold,
            ),
        )

val Typography
    @Composable get() =
        Typography(
            displayLarge = TextStyle(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 56.sp,
                lineHeight = 53.sp,
                letterSpacing = (-3).sp,
                color = AccessDefaults.TextPrimary,
            ),

            displayMedium = TextStyle(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 42.sp,
                letterSpacing = (-2).sp,
                color = AccessDefaults.TextPrimary,
            ),

            displaySmall = TextStyle(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                letterSpacing = (-1).sp,
                color = AccessDefaults.TextPrimary,
            ),

            headlineLarge = TextStyle(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.7).sp,
                color = AccessDefaults.TextPrimary,
            ),

            headlineMedium = TextStyle(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.4).sp,
                color = AccessDefaults.TextPrimary,
            ),

            headlineSmall = TextStyle(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                letterSpacing = (-0.2).sp,
                color = AccessDefaults.TextPrimary,
            ),

            titleLarge = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.1).sp,
            ),

            titleMedium = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                letterSpacing = (-0.1).sp,
            ),

            titleSmall = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = (-0.1).sp,
            ),

            bodyLarge = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 25.sp,
                letterSpacing = (-0.1).sp,
            ),

            bodyMedium = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.2).sp,
            ),

            bodySmall = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.1).sp,
            ),

            labelLarge = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = (-0.1).sp,
            ),

            labelMedium = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.1).sp,
            ),

            labelSmall = TextStyle(
                fontFamily = JetbrainsMono,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.6.sp,
            ),
        )

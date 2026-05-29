package com.kaanf.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    val primaryHover: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,
    val textPrimary: Color,
    val textTertiary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val overlay: Color,
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentLightBlue: Color,
    val accentGrey: Color,
    val cakeViolet: Color,
    val cakeGreen: Color,
    val cakeBlue: Color,
    val cakePink: Color,
    val cakeOrange: Color,
    val cakeYellow: Color,
    val cakeTeal: Color,
    val cakePurple: Color,
    val cakeRed: Color,
    val cakeMint: Color,
    val iconPrimary: Color,
    val buttonDisabledBg: Color,
    val buttonDefaultBg: Color,
    val buttonFocusedBg: Color,
    val buttonOutline: Color,
    val buttonFocusedOutline: Color,
    val buttonPlaceholderTextColor: Color,
    val buttonFocusedTextColor: Color,
    val buttonDisabledTextColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
)

val LightExtendedColors =
    ExtendedColors(
        primaryHover = Primary400,
        destructiveHover = ChirpRed600,
        destructiveSecondaryOutline = ChirpRed200,
        disabledOutline = ChirpBase200,
        disabledFill = Primary200,
        successOutline = ChirpBrand100,
        success = ChirpBrand600,
        onSuccess = ChirpBase0,
        secondaryFill = ChirpBase100,
        textPrimary = ChirpBase1000,
        textTertiary = ChirpBase800,
        textSecondary = ChirpBase900,
        textPlaceholder = ChirpBase700,
        textDisabled = White,
        surfaceLower = ChirpBase100,
        surfaceHigher = ChirpBase100,
        surfaceOutline = ChirpBase1000Alpha14,
        overlay = ChirpBase1000Alpha80,
        accentBlue = ChirpBlue,
        accentPurple = ChirpPurple,
        accentViolet = ChirpViolet,
        accentPink = ChirpPink,
        accentOrange = ChirpOrange,
        accentYellow = ChirpYellow,
        accentGreen = ChirpGreen,
        accentTeal = ChirpTeal,
        accentLightBlue = ChirpLightBlue,
        accentGrey = ChirpGrey,
        cakeViolet = ChirpCakeLightViolet,
        cakeGreen = ChirpCakeLightGreen,
        cakeBlue = ChirpCakeLightBlue,
        cakePink = ChirpCakeLightPink,
        cakeOrange = ChirpCakeLightOrange,
        cakeYellow = ChirpCakeLightYellow,
        cakeTeal = ChirpCakeLightTeal,
        cakePurple = ChirpCakeLightPurple,
        cakeRed = ChirpCakeLightRed,
        cakeMint = ChirpCakeLightMint,
        iconPrimary = Neutral900,
        buttonOutline = Divider,
        buttonFocusedOutline = Primary400,
        buttonPlaceholderTextColor = Neutral300,
        buttonFocusedTextColor = Neutral900,
        buttonDisabledTextColor = Neutral300,
        buttonDisabledBg = Neutral200,
        buttonDefaultBg = Transparent,
        buttonFocusedBg = Primary50,
        primaryTextColor = Neutral900,
        secondaryTextColor = Neutral300,
    )

val DarkExtendedColors =
    ExtendedColors(
        primaryHover = ChirpBrand600,
        destructiveHover = ChirpRed600,
        destructiveSecondaryOutline = ChirpRed200,
        disabledOutline = ChirpBase900,
        disabledFill = Primary200,
        successOutline = ChirpBrand500Alpha40,
        success = ChirpBrand500,
        onSuccess = ChirpBase1000,
        secondaryFill = ChirpBase900,
        textPrimary = ChirpBase0,
        textTertiary = ChirpBase200,
        textSecondary = ChirpBase150,
        textPlaceholder = ChirpBase400,
        textDisabled = White,
        surfaceLower = ChirpBase1000,
        surfaceHigher = ChirpBase900,
        surfaceOutline = ChirpBase100Alpha10Alt,
        overlay = ChirpBase1000Alpha80,
        accentBlue = ChirpBlue,
        accentPurple = ChirpPurple,
        accentViolet = ChirpViolet,
        accentPink = ChirpPink,
        accentOrange = ChirpOrange,
        accentYellow = ChirpYellow,
        accentGreen = ChirpGreen,
        accentTeal = ChirpTeal,
        accentLightBlue = ChirpLightBlue,
        accentGrey = ChirpGrey,
        iconPrimary = White,
        cakeViolet = ChirpCakeDarkViolet,
        cakeGreen = ChirpCakeDarkGreen,
        cakeBlue = ChirpCakeDarkBlue,
        cakePink = ChirpCakeDarkPink,
        cakeOrange = ChirpCakeDarkOrange,
        cakeYellow = ChirpCakeDarkYellow,
        cakeTeal = ChirpCakeDarkTeal,
        cakePurple = ChirpCakeDarkPurple,
        cakeRed = ChirpCakeDarkRed,
        cakeMint = ChirpCakeDarkMint,
        buttonOutline = Neutral300,
        buttonFocusedOutline = Primary400,
        buttonPlaceholderTextColor = Neutral200,
        buttonFocusedTextColor = White,
        buttonDisabledTextColor = Neutral200,
        buttonDisabledBg = Neutral200,
        buttonDefaultBg = Transparent,
        buttonFocusedBg = Neutral800,
        primaryTextColor = White,
        secondaryTextColor = Neutral200,
    )

val LightColorScheme =
    lightColorScheme(
        primary = Primary400,
        onPrimary = White,
        primaryContainer = ChirpBrand100,
        onPrimaryContainer = ChirpBrand900,
        secondary = ChirpBase700,
        onSecondary = ChirpBase0,
        secondaryContainer = ChirpBase100,
        onSecondaryContainer = ChirpBase900,
        tertiary = ChirpBrand900,
        onTertiary = ChirpBase0,
        tertiaryContainer = ChirpBrand100,
        onTertiaryContainer = ChirpBrand1000,
        error = ChirpRed500,
        onError = ChirpBase0,
        errorContainer = ChirpRed200,
        onErrorContainer = ChirpRed600,
        background = White,
        onBackground = ChirpBase0,
        surface = ChirpBase0,
        onSurface = ChirpBase1000,
        surfaceVariant = ChirpBase100,
        onSurfaceVariant = ChirpBase900,
        outline = Divider,
        outlineVariant = ChirpBase200,
    )

val DarkColorScheme =
    darkColorScheme(
        primary = Primary400,
        onPrimary = White,
        primaryContainer = ChirpBrand900,
        onPrimaryContainer = ChirpBrand500,
        secondary = ChirpBase400,
        onSecondary = ChirpBase1000,
        secondaryContainer = ChirpBase900,
        onSecondaryContainer = ChirpBase150,
        tertiary = ChirpBrand500,
        onTertiary = ChirpBase1000,
        tertiaryContainer = ChirpBrand900,
        onTertiaryContainer = ChirpBrand500,
        error = ChirpRed500,
        onError = ChirpBase0,
        errorContainer = ChirpRed600,
        onErrorContainer = ChirpRed200,
        background = Black,
        onBackground = ChirpBase0,
        surface = ChirpBase950,
        onSurface = ChirpBase0,
        surfaceVariant = ChirpBase900,
        onSurfaceVariant = ChirpBase150,
        outline = Divider,
        outlineVariant = ChirpBase800,
    )

/** **/

// Crew CSS :root renkleri — birebir karşılıklar
// Kaynak: crew.css

// Base
val CrewBg = Color(0xFF0E0B08)              // --bg: App ana background / phone background
val CrewSurface = Color(0xFF181410)         // --surface: Card, panel, dark QR card
val CrewSurface2 = Color(0xFF211B15)        // --surface-2: Input, back button, chip, secondary button
val CrewSurface3 = Color(0xFF2C241D)        // --surface-3: Stronger surface / scrollbar / depth

// Borders
val CrewBorder = Color(0xFF2E2620)          // --border: Input border, card strong border, ghost button border
val CrewBorderSoft = Color(0xFF221C17)      // --border-soft: AppBar button border, cards, dividers

// Text
val CrewText = Color(0xFFF6EFE3)            // --text: Primary text, icons, appbar title
val CrewText2 = Color(0xFFC8BFB1)           // --text-2: Secondary readable text, chip text
val CrewTextDim = Color(0xFF837A6E)         // --text-dim: Muted text, labels, helper text
val CrewTextFaint = Color(0xFF524A41)       // --text-faint: Very low emphasis text

// Accent
val CrewAccent = Color(0xFFC8FF3D)          // --accent: Main CTA, brand mark, active states
val CrewAccentInk = Color(0xFF0E0B08)       // --accent-ink: Text/icon color on accent background
val CrewAccentGlow = Color(0x59C8FF3D)      // --accent-glow: rgba(200,255,61,0.35), glow/shadow

// Semantic / game accents
val CrewCoral = Color(0xFFFF7A5C)           // --coral: Warning/error-ish, warm highlight
val CrewAmber = Color(0xFFFFB341)           // --amber: Social/team phase, warm badge
val CrewRose = Color(0xFFFF5A7A)            // --rose: Danger, live, final round
val CrewTeal = Color(0xFF5BE0C5)            // --teal: Confetti / alternative accent
val CrewMint = Color(0xFF6BE7A5)            // --mint: Success / signal / positive state
val CrewSky = Color(0xFF6FB7FF)             // --sky: Social phase / blue accent

val CrewBlack = Color(0xFF000000)           // Notch / pure black
val CrewQrInk = Color(0xFF15110C)           // QR dark cells/text
val CrewEventHeroBase = Color(0xFF1A1410)   // Event hero base background
val CrewScannerBase = Color(0xFF050402)     // Scanner camera dark base

val CrewWhite04 = Color(0x0AFFFFFF)         // rgba(255,255,255,0.04): subtle inset/borders
val CrewWhite06 = Color(0x0FFFFFFF)         // rgba(255,255,255,0.06): soft borders
val CrewWhite18 = Color(0x2EFFFFFF)         // rgba(255,255,255,0.18): CTA inset shine
val CrewWhite22 = Color(0x38FFFFFF)         // rgba(255,255,255,0.22): home indicator
val CrewBlack45 = Color(0x73000000)         // rgba(0,0,0,0.45): bottom tab shadow
val CrewBlack55 = Color(0x8C000000)         // rgba(0,0,0,0.55): scanner/phone shadows

val CrewTabBarBg = Color(0xC714100C)        // rgba(20,16,12,0.78): bottom tab blurred bg
val CrewRose12 = Color(0x1FFF5A7A)          // rgba(255,90,122,0.12): live chip bg
val CrewRose25 = Color(0x40FF5A7A)          // rgba(255,90,122,0.25): danger border
val CrewCoral22 = Color(0x38FF7A5C)         // rgba(255,122,92,0.22): event hero gradient

@Immutable
data class CrewColors(
    // Background / surfaces
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,

    // Borders
    val border: Color,
    val borderSoft: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textDim: Color,
    val textFaint: Color,

    // Brand / CTA
    val accent: Color,
    val onAccent: Color,
    val accentGlow: Color,

    // Semantic accents
    val coral: Color,
    val amber: Color,
    val rose: Color,
    val teal: Color,
    val mint: Color,
    val sky: Color,

    // Component-specific
    val appBarBackBg: Color,
    val appBarBackBorder: Color,
    val appBarIcon: Color,

    val cardBg: Color,
    val cardBorder: Color,

    val inputBg: Color,
    val inputBorder: Color,
    val inputText: Color,
    val inputPlaceholder: Color,

    val buttonPrimaryBg: Color,
    val buttonPrimaryText: Color,
    val buttonGhostBg: Color,
    val buttonGhostText: Color,
    val buttonGhostBorder: Color,
    val buttonDangerText: Color,
    val buttonDangerBorder: Color,

    val chipBg: Color,
    val chipText: Color,
    val chipBorder: Color,

    val bottomTabBg: Color,
    val bottomTabActiveBg: Color,
    val bottomTabText: Color,
    val bottomTabActiveText: Color,

    val qrCardBg: Color,
    val qrCardInk: Color,
)

val CrewExtendedColors =
    CrewColors(
        background = CrewBg,
        surface = CrewSurface,
        surface2 = CrewSurface2,
        surface3 = CrewSurface3,

        border = CrewBorder,
        borderSoft = CrewBorderSoft,

        textPrimary = CrewText,
        textSecondary = CrewText2,
        textDim = CrewTextDim,
        textFaint = CrewTextFaint,

        accent = CrewAccent,
        onAccent = CrewAccentInk,
        accentGlow = CrewAccentGlow,

        coral = CrewCoral,
        amber = CrewAmber,
        rose = CrewRose,
        teal = CrewTeal,
        mint = CrewMint,
        sky = CrewSky,

        // .appbar .back: background var(--surface-2), border var(--border-soft), color var(--text)
        appBarBackBg = CrewSurface2,
        appBarBackBorder = CrewBorderSoft,
        appBarIcon = CrewText,

        // .card: background var(--surface), border var(--border-soft)
        cardBg = CrewSurface,
        cardBorder = CrewBorderSoft,

        // .input: background var(--surface-2), border var(--border), text var(--text), placeholder var(--text-faint)
        inputBg = CrewSurface2,
        inputBorder = CrewBorder,
        inputText = CrewText,
        inputPlaceholder = CrewTextFaint,

        // .btn: background var(--accent), color var(--accent-ink)
        buttonPrimaryBg = CrewAccent,
        buttonPrimaryText = CrewAccentInk,

        // .btn.ghost: background transparent, color var(--text), border var(--border)
        buttonGhostBg = Transparent,
        buttonGhostText = CrewText,
        buttonGhostBorder = CrewBorder,

        // .btn.danger: color var(--rose), border rgba(255,90,122,0.25)
        buttonDangerText = CrewRose,
        buttonDangerBorder = CrewRose25,

        // .chip: background var(--surface-2), color var(--text-2), border var(--border-soft)
        chipBg = CrewSurface2,
        chipText = CrewText2,
        chipBorder = CrewBorderSoft,

        // .tabbar / .tab.active
        bottomTabBg = CrewTabBarBg,
        bottomTabActiveBg = CrewSurface2,
        bottomTabText = CrewTextDim,
        bottomTabActiveText = CrewText,

        // .qr-card
        qrCardBg = CrewText,
        qrCardInk = CrewQrInk,
    )

val CrewDarkColorScheme =
    darkColorScheme(
        primary = CrewAccent,
        onPrimary = CrewAccentInk,

        primaryContainer = CrewSurface2,
        onPrimaryContainer = CrewText,

        secondary = CrewText2,
        onSecondary = CrewBg,

        secondaryContainer = CrewSurface,
        onSecondaryContainer = CrewText2,

        tertiary = CrewCoral,
        onTertiary = CrewBg,

        error = CrewRose,
        onError = CrewBg,

        background = CrewBg,
        onBackground = CrewText,

        surface = CrewSurface,
        onSurface = CrewText,

        surfaceVariant = CrewSurface2,
        onSurfaceVariant = CrewText2,

        outline = CrewBorder,
        outlineVariant = CrewBorderSoft,
    )

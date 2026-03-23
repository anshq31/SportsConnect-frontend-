package com.ansh.sportsapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary          = SportGreen,
    onPrimary        = Color(0xFF0A0C0F),
    primaryContainer = SportGreenContainer,
    onPrimaryContainer = SportGreen,

    secondary        = TertiaryIndigo,
    onSecondary      = Color.White,
    secondaryContainer = TertiaryContainer,
    onSecondaryContainer = TertiaryIndigo,

    tertiary         = WarningAmber,
    onTertiary       = Color(0xFF0A0C0F),
    tertiaryContainer = WarningContainer,
    onTertiaryContainer = WarningAmber,

    error            = ErrorRed,
    errorContainer   = ErrorContainer,
    onError          = Color.White,

    background       = BackgroundDark,
    onBackground     = OnSurface,
    surface          = SurfaceDark,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariant,
    outline          = Outline,
    outlineVariant   = OutlineVariant,
    scrim            = Color(0xFF000000),
)
private val LightColorScheme = lightColorScheme(

    primary = IndiaBlue,
    onPrimary = Color.White,

    secondary = FieldGreen,
    onSecondary = Color.White,

    tertiary = SaffronEnergy,
    onTertiary = Color.White,

    background = NeutralLightBg,
    onBackground = TextDark,

    surface = CardLight,
    onSurface = TextDark,

    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun SportsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = SportsShapes,
        content = content
    )
}
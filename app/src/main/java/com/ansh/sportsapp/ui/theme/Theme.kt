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

    primary = IndiaBlueLight,
    onPrimary = Color.White,

    secondary = TurfGreenLight,
    onSecondary = Color.Black,

    tertiary = SaffronEnergy,
    onTertiary = Color.Black,

    background = NeutralDarkBg,
    onBackground = TextLight,

    surface = CardDark,
    onSurface = TextLight,

    error = Color(0xFFFF5252),
    onError = Color.Black
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
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
    primary = SportsGreen,
    onPrimary = Color.Black,

    secondary = AccentBlue,
    onSecondary = Color.White,

    tertiary = AccentOrange,
    onTertiary = Color.Black,

    background = StadiumDark,
    onBackground = Color.White,

    surface = SurfaceDark,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SportsGreen,
    onPrimary = Color.White,

    secondary = AccentBlue,
    onSecondary = Color.White,

    tertiary = AccentOrange,
    onTertiary = Color.White,

    background = SurfaceLight,
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A)
)

@Composable
fun SportsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SportsShapes,
        content = content
    )
}
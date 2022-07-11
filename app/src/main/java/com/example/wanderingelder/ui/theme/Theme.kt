package com.example.wanderingelder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = primaryDark,
    primaryVariant = primaryVariantDark,
    secondary = secondaryDark,
    background = Color.Gray
)

private val LightColorPalette = lightColors(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryVariant = primaryVariantDark,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    background = backgroundColor,
    onBackground = onBackgroundColor,
    surface = surfaceColorLight,
    onSurface = onSurfaceColorLight

)

@Composable
fun WanderingElderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
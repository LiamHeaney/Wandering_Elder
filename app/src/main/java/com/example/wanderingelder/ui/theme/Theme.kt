package com.example.wanderingelder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//This is a mapping of colors to the DarkTheme
//Currently, it is no different than the light theme
private val DarkColorPalette = darkColors(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryVariant = primaryVariantLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    background = backgroundColor,
    onBackground = onBackgroundColor,
    surface = surfaceColorLight,
    onSurface = onSurfaceColorLight
)
//This is a mapping of colors to the LightTheme
private val LightColorPalette = lightColors(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryVariant = primaryVariantLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    background = backgroundColor,
    onBackground = onBackgroundColor,
    surface = surfaceColorLight,
    onSurface = onSurfaceColorLight

)

//This selects dark or light theme based on the setting of the phone
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
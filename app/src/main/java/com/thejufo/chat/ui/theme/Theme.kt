package com.thejufo.chat.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColors()

private val LightColorScheme = lightColors(
    primary = blue,
    onPrimary = white,
    secondary = blue,
    onSecondary = white,
    background = white,
    onBackground = darkGray,
    surface = white,
    onSurface = darkGray,
)

@Composable
fun ChatTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}
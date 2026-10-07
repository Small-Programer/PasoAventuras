package com.example.pasoaventuras.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PasoColorScheme = lightColorScheme(
    primary = Indigo500,
    onPrimary = AppWhite,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo900,
    secondary = Emerald500,
    onSecondary = AppWhite,
    secondaryContainer = Emerald100,
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Amber500,
    onTertiary = AppWhite,
    tertiaryContainer = Amber100,
    onTertiaryContainer = Amber800,
    background = Slate50,
    onBackground = Slate900,
    surface = AppWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate100,
    error = Red500,
    onError = AppWhite,
)

@Composable
fun PasoAventurasTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PasoColorScheme,
        typography = Typography,
        content = content
    )
}

package com.ivianuu.essentials.ui.compose.material

import androidx.ui.graphics.Color
import androidx.ui.material.MaterialColors

fun DarkMaterialColors(
    primary: Color = Color(0xFFBB86FC.toInt()),
    primaryVariant: Color = Color(0xFF3700B3.toInt()),
    secondary: Color = Color(0xFF03DAC6.toInt()),
    secondaryVariant: Color = Color(0xFF03DAC6.toInt()),
    background: Color = Color(0xFF121212.toInt()),
    surface: Color = Color(0xFF121212.toInt()),
    error: Color = Color(0xFFCF6679.toInt()),
    onPrimary: Color = Color.Black,
    onSecondary: Color = Color.Black,
    onBackground: Color = Color.White,
    onSurface: Color = Color.White,
    onError: Color = Color.Black
) = MaterialColors(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
)
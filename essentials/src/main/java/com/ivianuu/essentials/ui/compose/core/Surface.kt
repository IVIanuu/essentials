package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Ambient
import androidx.compose.ViewComposition
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.graphics.Color
import androidx.ui.material.textColorForBackground
import androidx.ui.material.themeColor
import androidx.ui.text.TextStyle

fun ViewComposition.Surface(
    color: Color = +themeColor { surface },
    children: ViewComposition.() -> Unit
) {
    RippleSurfaceAmbient.Provider(value = color) {
        CurrentTextStyleProvider(value = TextStyle(color = +textColorForBackground(color))) {
            children()
        }
    }
}

val RippleSurfaceAmbient = Ambient.of<Color>("RippleSurfaceAmbient")
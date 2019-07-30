package com.ivianuu.essentials.ui.compose.core

import androidx.compose.ViewComposition
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.graphics.Color
import androidx.ui.material.textColorForBackground
import androidx.ui.material.themeColor
import androidx.ui.text.TextStyle
import com.ivianuu.essentials.ui.compose.material.RippleSurfaceAmbient

// todo better implement this

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
package com.ivianuu.essentials.ui.compose.material

import androidx.compose.ViewComposition
import androidx.compose.ambient
import androidx.ui.graphics.Color
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.themeColor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.View
import com.ivianuu.essentials.ui.compose.view.ViewDsl
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.imageColor
import com.ivianuu.essentials.ui.compose.view.set

inline fun ViewComposition.FloatingActionButton(noinline block: ViewDsl<FloatingActionButton>.() -> Unit) =
    FloatingActionButton(sourceLocation(), block)

fun ViewComposition.FloatingActionButton(
    key: Any,
    block: ViewDsl<FloatingActionButton>.() -> Unit
) =
    View(key, { FloatingActionButton(it) }) {
        val secondaryColor = +themeColor { secondary }
        backgroundColor(secondaryColor)
        imageColor(+themeColor { onSecondary })
        val rippleColor = (+ambient(CurrentRippleTheme)).colorCallback(secondaryColor)
        rippleColor(rippleColor)
        block()
    }

enum class FabSize {
    Normal, Mini;

    fun toSizeInt() = when (this) {
        Normal -> FloatingActionButton.SIZE_NORMAL
        Mini -> FloatingActionButton.SIZE_MINI
    }
}

fun <T : FloatingActionButton> ViewDsl<T>.size(size: FabSize) {
    set(size) { this.size = it.toSizeInt() }
}

fun <T : FloatingActionButton> ViewDsl<T>.rippleColor(color: Color) {
    set(color) { this.rippleColor = color.toArgb() }
}
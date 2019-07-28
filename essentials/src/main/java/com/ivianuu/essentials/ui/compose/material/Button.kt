package com.ivianuu.essentials.ui.compose.material

import android.content.res.ColorStateList
import androidx.compose.ViewComposition
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.withDensity
import androidx.ui.foundation.shape.border.Border
import androidx.ui.graphics.Color
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import androidx.ui.painting.Paint
import com.google.android.material.button.MaterialButton
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.View
import com.ivianuu.essentials.ui.compose.view.ViewDsl
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.set
import com.ivianuu.essentials.ui.compose.view.textStyle

// todo

inline fun ViewComposition.Button(noinline block: ViewDsl<MaterialButton>.() -> Unit) =
    Button(sourceLocation(), block)

fun ViewComposition.Button(key: Any, block: ViewDsl<MaterialButton>.() -> Unit) =
    View(key, { MaterialButton(it) }) {
        textStyle(+themeTextStyle { button })

        val primaryColor = +themeColor { primary }
        backgroundColor(primaryColor)

        val rippleTheme = +ambient(CurrentRippleTheme)
        rippleColor(rippleTheme.colorCallback(primaryColor))

        block()
    }

fun <T : MaterialButton> ViewDsl<T>.rippleColor(color: Color) {
    set(color) { rippleColor = ColorStateList.valueOf(it.toArgb()) }
}

private val dummyPaint = Paint()

fun <T : MaterialButton> ViewDsl<T>.border(border: Border) {
    +withDensity {
        set(border) {
            border.brush.applyBrush(dummyPaint)
            strokeColor = ColorStateList.valueOf(dummyPaint.color.toArgb())
            strokeWidth = border.width.toIntPx().value
        }
    }
}

fun <T : MaterialButton> ViewDsl<T>.border(
    color: Color,
    width: Dp
) {
    border(Border(color, width))
}
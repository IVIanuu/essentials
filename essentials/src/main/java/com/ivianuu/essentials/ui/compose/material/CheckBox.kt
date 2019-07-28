package com.ivianuu.essentials.ui.compose.material

import android.content.res.ColorStateList
import androidx.compose.ViewComposition
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor
import com.github.ajalt.timberkt.d
import com.google.android.material.checkbox.MaterialCheckBox
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.*

fun ViewComposition.CheckBox(
    value: Boolean,
    onChange: (Boolean) -> Unit
) {
    CheckBox {
        value(value)
        onChange(onChange)
    }
}

inline fun ViewComposition.CheckBox(noinline block: ViewDsl<MaterialCheckBox>.() -> Unit) =
    CheckBox(sourceLocation(), block)

fun ViewComposition.CheckBox(key: Any, block: ViewDsl<MaterialCheckBox>.() -> Unit) =
    View(key, { MaterialCheckBox(it) }) {
        color(+themeColor { secondary })
        block()
    }

fun <T : MaterialCheckBox> ViewDsl<T>.color(color: Color) {
    set(color) {
        with(composition) {
            buttonTintList = createColorStateList(color)
                .also {
                    d { "create color state list $it" }
                }
        }
    }
}

private fun ViewComposition.createColorStateList(color: Color): ColorStateList {
    val unselectedColor = (+themeColor { onSurface }).copy(alpha = UncheckedBoxOpacity)

    return ColorStateList(
        ENABLED_CHECKED_STATES,
        intArrayOf(
            color.toArgb(),
            unselectedColor.toArgb()
        )
    )
}


private val ENABLED_CHECKED_STATES = arrayOf(
    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
    intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked)/*,
    intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_checked),
    intArrayOf(-android.R.attr.state_enabled, -android.R.attr.state_checked)*/
)

private val UncheckedBoxOpacity = 0.6f
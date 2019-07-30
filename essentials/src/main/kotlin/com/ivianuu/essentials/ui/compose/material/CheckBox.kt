package com.ivianuu.essentials.ui.compose.material

import android.content.res.ColorStateList
import androidx.compose.ViewComposition
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor
import com.google.android.material.checkbox.MaterialCheckBox
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.View
import com.ivianuu.essentials.ui.compose.view.ViewDsl
import com.ivianuu.essentials.ui.compose.view.onChange
import com.ivianuu.essentials.ui.compose.view.set
import com.ivianuu.essentials.ui.compose.view.value
import com.ivianuu.essentials.ui.compose.view.wrapContent

// todo add disabled color

inline fun ViewComposition.CheckBox(
    checked: Boolean,
    noinline onCheckedChange: (Boolean) -> Unit
) = CheckBox(sourceLocation(), checked, onCheckedChange)

fun ViewComposition.CheckBox(
    key: Any,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    CheckBox(key) {
        value(checked)
        onChange(onCheckedChange)
    }
}

inline fun ViewComposition.CheckBox(noinline block: ViewDsl<MaterialCheckBox>.() -> Unit) =
    CheckBox(sourceLocation(), block)

fun ViewComposition.CheckBox(key: Any, block: ViewDsl<MaterialCheckBox>.() -> Unit) =
    View(key, { MaterialCheckBox(it) }) {
        wrapContent()
        rippleBackground(false)
        color(+themeColor { secondary })
        block()
    }

fun <T : MaterialCheckBox> ViewDsl<T>.color(color: Color) {
    val unselectedColor = with(composition) {
        (+themeColor { onSurface }).copy(alpha = UncheckedOpacity)
    }

    set(color) {
        buttonTintList = createColorStateList(color, unselectedColor)
    }
}

private fun createColorStateList(
    activeColor: Color,
    unselectedColor: Color
): ColorStateList {
    return ColorStateList(
        ENABLED_CHECKED_STATES,
        intArrayOf(
            activeColor.toArgb(),
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

private val UncheckedOpacity = 0.6f
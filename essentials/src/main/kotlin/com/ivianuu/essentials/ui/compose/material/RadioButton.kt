package com.ivianuu.essentials.ui.compose.material

import android.content.res.ColorStateList
import androidx.compose.ViewComposition
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor
import com.google.android.material.radiobutton.MaterialRadioButton
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.View
import com.ivianuu.essentials.ui.compose.view.ViewDsl
import com.ivianuu.essentials.ui.compose.view.onChange
import com.ivianuu.essentials.ui.compose.view.set
import com.ivianuu.essentials.ui.compose.view.value
import com.ivianuu.essentials.ui.compose.view.wrapContent

inline fun ViewComposition.RadioButton(selected: Boolean, noinline onSelect: () -> Unit) =
    RadioButton(sourceLocation(), selected, onSelect)

fun ViewComposition.RadioButton(
    key: Any,
    selected: Boolean,
    onSelect: () -> Unit
) {
    RadioButton(key) {
        value(selected)
        onChange { onSelect() }
    }
}

inline fun ViewComposition.RadioButton(noinline block: ViewDsl<MaterialRadioButton>.() -> Unit) =
    RadioButton(sourceLocation(), block)

fun ViewComposition.RadioButton(key: Any, block: ViewDsl<MaterialRadioButton>.() -> Unit) =
    View(key, { MaterialRadioButton(it) }) {
        wrapContent()
        rippleBackground(false)
        color(+themeColor { secondary })
        block()
    }

fun <T : MaterialRadioButton> ViewDsl<T>.color(color: Color) {
    val unselectedColor = with(composition) {
        (+themeColor { onSurface }).copy(alpha = UnselectedOpacity)
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

private val UnselectedOpacity = 0.6f
package com.ivianuu.essentials.ui.compose.view

import android.widget.CompoundButton

fun <T : CompoundButton> ViewDsl<T>.value(value: Boolean) {
    set(value) { isChecked = value }
}

fun <T : CompoundButton> ViewDsl<T>.onChange(onChange: (Boolean) -> Unit) {
    set(onChange) { setOnCheckedChangeListener { _, isChecked -> onChange(isChecked) } }
}
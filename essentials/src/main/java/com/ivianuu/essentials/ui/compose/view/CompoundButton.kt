package com.ivianuu.essentials.ui.compose.view

import android.widget.CompoundButton

fun <T : CompoundButton> ViewDsl<T>.value(checked: Boolean) {
    set(checked) { isChecked = checked }
}

fun <T : CompoundButton> ViewDsl<T>.onChange(onChange: (Boolean) -> Unit) {
    set(onChange) {
        setOnClickListener { onChange(isChecked) }
    }
}
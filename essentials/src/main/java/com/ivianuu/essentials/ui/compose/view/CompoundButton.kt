package com.ivianuu.essentials.ui.compose.view

import android.widget.CompoundButton
import androidx.ui.androidview.adapters.setControlledChecked
import androidx.ui.androidview.adapters.setOnCheckedChange

fun <T : CompoundButton> ViewDsl<T>.value(checked: Boolean) {
    set(checked) { setControlledChecked(it) }
}

fun <T : CompoundButton> ViewDsl<T>.onChange(onChange: (Boolean) -> Unit) {
    set(onChange) { setOnCheckedChange(onChange) }
}
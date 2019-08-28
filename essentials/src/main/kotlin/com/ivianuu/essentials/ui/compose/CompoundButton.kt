package com.ivianuu.essentials.ui.compose

import android.widget.CompoundButton
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.compose.set
import com.ivianuu.kprefs.Pref

fun ComponentComposition.CompoundButton(
    layoutRes: Int,
    pref: Pref<Boolean>,
    onChangePredicate: ((Boolean) -> Boolean)? = null
) {
    CompoundButton(
        layoutRes = layoutRes,
        value = pref.get(),
        onChange = { newValue ->
            if (onChangePredicate?.invoke(newValue) ?: true) {
                pref.set(newValue)
            }
        }
    )
}

fun ComponentComposition.CompoundButton(
    layoutRes: Int,
    value: Boolean,
    onChange: (Boolean) -> Unit
) {
    ViewByLayoutRes<CompoundButton>(layoutRes = layoutRes) {
        set(value) {
            isChecked = value
            setOnClickListener { onChange(!value) }
        }
    }
}
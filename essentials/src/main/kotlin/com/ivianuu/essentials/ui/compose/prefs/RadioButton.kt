package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.ui.material.RadioButton
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun RadioButtonPreference(
    pref: Pref<Boolean>,
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((Boolean) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("RadioButtonPreference") {
    fun valueChanged(newValue: Boolean) {
        if (onChange?.invoke(newValue) != false) {
            pref.set(newValue)
        }
    }

    Preference(
        pref = pref,
        title = title,
        summary = summary,
        singleLineSummary = singleLineSummary,
        leading = leading,
        trailing = {
            val onSelect: (() -> Unit)? = if (dependencies.checkAll()) {
                { valueChanged(!pref.get()) }
            } else {
                null
            }
            RadioButton(
                selected = pref.get(),
                onSelect = onSelect
            )
        },
        onClick = { valueChanged(!pref.get()) },
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}
package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.material.Switch
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun SwitchPreference(
    pref: Pref<Boolean>,
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((Boolean) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("SwitchPreference") {
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
            val onCheckedChange: ((Boolean) -> Unit)? = if (dependencies.checkAll()) {
                { valueChanged(it) }
            } else {
                null
            }
            Switch(
                color = +themeColor { secondary },
                checked = pref.get(),
                onCheckedChange = onCheckedChange
            )
        },
        onClick = { valueChanged(!pref.get()) },
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}
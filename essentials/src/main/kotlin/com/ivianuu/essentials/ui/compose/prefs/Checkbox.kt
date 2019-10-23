package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.material.Checkbox
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun CheckboxPreference(
    pref: Pref<Boolean>,
    text: @Composable() (() -> Unit),
    icon: @Composable() (() -> Unit)? = null,
    secondaryText: @Composable() (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable() (() -> Unit)? = null
) = composable("CheckboxPreference") {
    val dependencies = +ambient(DependenciesAmbient)

    Preference(
        text = text,
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = {
            Checkbox(
                checked = pref.get(),
                onCheckedChange = if (dependencies.allOk()) {
                    { newValue: Boolean -> pref.set(newValue) }
                } else {
                    null
                }
            )
        },
        onClick = { pref.set(!pref.get()) }
    )
}
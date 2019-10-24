package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.ui.core.Opacity
import androidx.ui.material.ListItem
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref


@Composable
fun <T> Preference(
    pref: Pref<T>,
    title: @Composable() (() -> Unit),
    summary: @Composable() (() -> Unit)? = null,
    singleLineSummary: Boolean = true,
    leading: @Composable() (() -> Unit)? = null,
    trailing: @Composable() (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("Preference:${pref.key}") {
    val finalEnabled = enabled && dependencies?.checkAll() ?: true

    Opacity(if (finalEnabled) 1f else 0.5f) {
        ListItem(
            text = title,
            secondaryText = summary,
            icon = leading,
            trailing = trailing,
            singleLineSecondaryText = singleLineSummary,
            onClick = if (finalEnabled) onClick else null
        )
    }
}
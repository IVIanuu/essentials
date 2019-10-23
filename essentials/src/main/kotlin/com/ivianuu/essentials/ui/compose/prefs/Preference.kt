package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.material.ListItem
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Preference(
    text: @Composable() (() -> Unit),
    icon: @Composable() (() -> Unit)? = null,
    secondaryText: @Composable() (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable() (() -> Unit)? = null,
    trailing: @Composable() (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) = composable("Preference") {
    val dependenciesOk = (+ambient(DependenciesAmbient)).allOk()

    ListItem(
        text = text,
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = trailing,
        onClick = if (dependenciesOk) onClick else null
    )
}
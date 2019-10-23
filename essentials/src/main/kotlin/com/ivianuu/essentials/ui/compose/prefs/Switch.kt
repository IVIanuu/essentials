package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.graphics.Color
import androidx.ui.material.ListItem
import androidx.ui.material.Switch
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun Switch(
    pref: Pref<Boolean>,
    color: Color = +themeColor { secondary }
) = composable("PrefSwitch") {
    Switch(
        checked = pref.get(),
        onCheckedChange = { pref.set(it) },
        color = color
    )
}

@Composable
fun SwitchPreference(
    pref: Pref<Boolean>,
    text: @Composable() (() -> Unit),
    icon: @Composable() (() -> Unit)? = null,
    secondaryText: @Composable() (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable() (() -> Unit)? = null
) = composable("SwitchPreference") {
    ListItem(
        text = text,
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = { Switch(pref = pref) },
        onClick = { pref.set(!pref.get()) }
    )
}
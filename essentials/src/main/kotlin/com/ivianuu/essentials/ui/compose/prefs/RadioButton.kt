package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.graphics.Color
import androidx.ui.material.ListItem
import androidx.ui.material.RadioButton
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun RadioButton(
    pref: Pref<Boolean>,
    color: Color = +themeColor { secondary }
) = composable("PrefRadioButton") {
    RadioButton(
        selected = pref.get(),
        onSelect = { pref.set(!pref.get()) },
        color = color
    )
}

@Composable
fun RadioButtonPreference(
    pref: Pref<Boolean>,
    text: @Composable() (() -> Unit),
    icon: @Composable() (() -> Unit)? = null,
    secondaryText: @Composable() (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable() (() -> Unit)? = null
) = composable("RadioButtonPreference") {
    ListItem(
        text = text,
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = { RadioButton(pref = pref) },
        onClick = { pref.set(!pref.get()) }
    )
}
package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.material.Slider
import com.ivianuu.kprefs.Pref

@Composable
fun Slider(
    pref: Pref<Int>,
    min: Int = 0,
    max: Int = 100,
    divisions: Int? = null
) = composable("PrefSlider") {
    Slider(
        value = pref.get(),
        onChanged = { pref.set(it) },
        min = min,
        max = max,
        divisions = divisions
    )
}

/**
@Composable
fun SliderListItem(
slider: @Composable() (() -> Unit),
text: @Composable() (() -> Unit),
icon: @Composable() (() -> Unit)? = null,
secondaryText: @Composable() (() -> Unit)? = null,
singleLineSecondaryText: Boolean = true,
overlineText: @Composable() (() -> Unit)? = null
) = composable("SliderListItem") {
Stack {
ListItem(text, icon, secondaryText, singleLineSecondaryText, overlineText)

Padding(left = 16.dp, top = 8.dp, right = 16.dp, bottom = 16.dp) {
slider()
}
}
}*/
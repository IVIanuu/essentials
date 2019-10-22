package com.ivianuu.essentials.ui.compose.material

import androidx.compose.unaryPlus
import androidx.ui.graphics.Color
import androidx.ui.material.Checkbox
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

fun Checkbox(
    pref: Pref<Boolean>,
    color: Color = +themeColor { secondary }
) = composable("PrefCheckbox") {
    Checkbox(
        checked = pref.get(),
        onCheckedChange = { pref.set(it) },
        color = color
    )
}
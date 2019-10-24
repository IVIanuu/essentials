package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.common.ListScreen
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun PrefsScreen(
    appBar: (@Composable() () -> Unit)? = null,
    prefs: @Composable() () -> Unit
) = composable("PrefsScreen") {
    Prefs {
        ListScreen(appBar, prefs)
    }
}
package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.material.Subheader

@Composable
fun PreferenceSubheader(
    text: String,
    dependencies: List<Dependency<*>>? = null
) = composable("PreferenceSubheader:$text") {
    Dependencies(dependencies) {
        Subheader(text)
    }
}

@Composable
fun PreferenceSubheader(
    dependencies: List<Dependency<*>>? = null,
    text: @Composable() () -> Unit
) = composable("PreferenceSubheader") {
    Dependencies(dependencies) {
        Subheader(text)
    }
}
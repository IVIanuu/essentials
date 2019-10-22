package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.Column
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.core.composable

fun Scaffold(
    appBar: (@Composable() () -> Unit)? = null,
    content: (@Composable() () -> Unit)? = null
) = composable("Scaffold") {
    Column {
        if (appBar != null) {
            Surface(elevation = 4.dp) {
                appBar()
            }
        }

        if (content != null) {
            content()
        }
    }
}
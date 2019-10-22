package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.layout.FlexColumn
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.core.composable

fun Scaffold(
    appBar: @Composable() () -> Unit,
    content: @Composable() () -> Unit
) = composable("Scaffold") {
    FlexColumn {
        inflexible {
            Surface(elevation = 4.dp) {
                appBar()
            }
        }

        flexible(1f) {
            Surface {
                content()
            }
        }
    }
}
package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.Padding
import androidx.ui.layout.Stack
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.core.composable

fun Scaffold(
    appBar: @Composable() () -> Unit,
    content: @Composable() () -> Unit,
    fabConfiguration: Scaffold.FabConfiguration? = null
) = composable("Scaffold") {
    Stack {
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

        if (fabConfiguration != null) {
            aligned(
                when (fabConfiguration.position) {
                    Scaffold.FabPosition.Center -> Alignment.BottomCenter
                    Scaffold.FabPosition.End -> Alignment.BottomRight
                }
            ) {
                Padding(padding = 16.dp) {
                    fabConfiguration.fab()
                }
            }
        }
    }
}

object Scaffold {

    data class FabConfiguration(
        val position: FabPosition,
        val fab: @Composable() () -> Unit
    )

    enum class FabPosition {
        Center, End
    }
}
package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Composable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.material.Scaffold

@Composable
fun ListScreen(
    appBar: (@Composable() () -> Unit)? = null,
    listContent: @Composable() () -> Unit
) = composable("ListScreen") {
    Scaffold(
        appBar = { appBar?.invoke() },
        content = {
            VerticalScroller {
                Column {
                    listContent()
                }
            }
        }
    )
}
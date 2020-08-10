package com.ivianuu.essentials.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.shape.corner.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElement
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar

@Composable
fun SharedElementPage(color: Color) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shared Elements") }) }
    ) {
        Stack {
            SharedElement(tag = "b", modifier = Modifier.center()) {
                Box(
                    backgroundColor = color,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(150.dp)
                )
            }
        }
    }
}

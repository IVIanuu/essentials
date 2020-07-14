package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Stack
import androidx.ui.layout.size
import androidx.ui.unit.dp
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

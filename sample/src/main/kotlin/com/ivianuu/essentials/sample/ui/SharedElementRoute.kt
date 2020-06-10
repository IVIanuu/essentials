package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Stack
import androidx.ui.layout.size
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.animatable.animatableElement
import com.ivianuu.essentials.ui.animatable.withValue
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElementKey
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route

@Composable
fun SharedElement(
    tag: Any,
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .animatableElement(tag, SharedElementKey withValue children) + modifier,
        children = children
    )
}

fun SharedElementRoute(color: Color) = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Shared Elements") }) },
        body = {
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
    )
}

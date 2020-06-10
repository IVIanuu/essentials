package com.ivianuu.essentials.sample.ui

import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
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

fun SharedElementRoute(color: Color) = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Shared Elements") }) },
        body = {
            Box(
                backgroundColor = color,
                shape = CircleShape,
                modifier = Modifier
                    .center()
                    .size(150.dp)
                    .animatableElement("b", SharedElementKey withValue {
                        Box(
                            backgroundColor = color,
                            shape = CircleShape,
                            modifier = Modifier
                                .center()
                                .size(150.dp)
                        )
                    })
            )
        }
    )
}

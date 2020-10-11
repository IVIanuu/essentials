package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElement
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding

@FunBinding
@Composable
fun SharedElementPage(color: @Assisted Color) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shared Elements") }) }
    ) {
        Box {
            SharedElement(tag = "b", modifier = Modifier.center()) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(color, CircleShape)
                )
            }
        }
    }
}

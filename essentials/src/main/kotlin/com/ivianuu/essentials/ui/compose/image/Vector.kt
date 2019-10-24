package com.ivianuu.essentials.ui.compose.image

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.WithDensity
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.Container
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun VectorImage(
    resId: Int,
    tint: Color = Color.Transparent
) = composable("VectorImage") {
    val vector = +vectorResource(resId)
    WithDensity {
        Container(
            width = vector.defaultWidth.toDp(),
            height = vector.defaultHeight.toDp()
        ) {
            DrawVector(vector, tint)
        }
    }
}
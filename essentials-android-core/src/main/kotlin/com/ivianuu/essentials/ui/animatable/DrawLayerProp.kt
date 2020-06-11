package com.ivianuu.essentials.ui.animatable

import androidx.compose.Stable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.ui.core.DrawLayerModifier
import androidx.ui.core.TransformOrigin
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.Shape

@Stable
interface DrawLayerProp<T> : Prop<T> {
    fun apply(modifier: MutableDrawLayerModifier, value: T)
}

val ScaleX = drawLayerProp<Float> { scaleX = it }
val ScaleY = drawLayerProp<Float> { scaleY = it }
val Alpha = drawLayerProp<Float> { alpha = it }
val TranslationX = drawLayerProp<Float> { translationX = it }
val TranslationY = drawLayerProp<Float> { translationY = it }
val ShadowElevation = drawLayerProp<Float> { shadowElevation = it }
val RotationX = drawLayerProp<Float> { rotationX = it }
val RotationY = drawLayerProp<Float> { rotationY = it }
val RotationZ = drawLayerProp<Float> { rotationZ = it }
val TransformOrigin = drawLayerProp<TransformOrigin> { transformOrigin = it }
val Shape = drawLayerProp<Shape> { shape = it }
val Clip = drawLayerProp<Boolean> { clip = it }

inline fun <T> drawLayerProp(crossinline apply: MutableDrawLayerModifier.(T) -> Unit): DrawLayerProp<T> {
    return object : DrawLayerProp<T> {
        override fun apply(modifier: MutableDrawLayerModifier, value: T) {
            apply.invoke(modifier, value)
        }
    }
}

@Stable
class MutableDrawLayerModifier : DrawLayerModifier {
    override var scaleX by mutableStateOf(1f)
    override var scaleY by mutableStateOf(1f)
    override var alpha by mutableStateOf(1f)
    override var translationX by mutableStateOf(0f)
    override var translationY by mutableStateOf(0f)
    override var shadowElevation by mutableStateOf(0f)
    override var rotationX by mutableStateOf(0f)
    override var rotationY by mutableStateOf(0f)
    override var rotationZ by mutableStateOf(0f)
    override var transformOrigin by mutableStateOf(TransformOrigin.Center)
    override var shape by mutableStateOf(RectangleShape)
    override var clip by mutableStateOf(false)
}

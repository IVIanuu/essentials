/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.animatable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.TransformOrigin
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

@Stable
interface ModifierProp<T> : Prop<T> {
    fun asModifier(value: T): Modifier
}

inline fun <T> modifierProp(crossinline apply: (T) -> Modifier): ModifierProp<T> =
    object : ModifierProp<T> {
        override fun asModifier(value: T): Modifier = apply.invoke(value)
    }

inline fun <T> composedModifierProp(crossinline apply: @Composable (T) -> Modifier): ModifierProp<T> =
    modifierProp { value -> Modifier.composed { apply(value) } }


inline fun <T> drawLayerProp(crossinline apply: GraphicsLayerScope.(T) -> Unit): ModifierProp<T> =
    modifierProp { Modifier.graphicsLayer { apply(it) } }

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

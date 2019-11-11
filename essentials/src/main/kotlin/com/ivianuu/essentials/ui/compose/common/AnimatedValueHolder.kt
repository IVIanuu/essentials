/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.common

import androidx.animation.AnimatedFloat
import androidx.animation.ValueHolder
import androidx.annotation.CheckResult
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.lerp

// todo remove once in compose

class AnimatedValueHolder(initial: Float) : ValueHolder<Float> {

    private val holder = ListeneableValueHolder(initial, { value = it })
    val animatedFloat = AnimatedFloat(holder)

    fun setBounds(min: Float = Float.NEGATIVE_INFINITY, max: Float = Float.POSITIVE_INFINITY) {
        animatedFloat.setBounds(min, max)
    }

    fun fling(config: FlingConfig, startVelocity: Float) {
        animatedFloat.fling(config, startVelocity)
    }

    override var value: Float by framed(initial)

    override val interpolator: (start: Float, end: Float, fraction: Float) -> Float
        get() = holder.interpolator
}

@CheckResult(suggest = "+")
fun animatedDragValue(initial: Float, minBound: Float, maxBound: Float) =
    effectOf<AnimatedValueHolder> {
        val vh = +memo { AnimatedValueHolder(initial) }
        vh.setBounds(minBound, maxBound)
        vh
    }

private class ListeneableValueHolder(
    var current: Float,
    var onValueChanged: (Float) -> Unit
) : ValueHolder<Float> {
    override val interpolator: (start: Float, end: Float, fraction: Float) -> Float = ::lerp
    override var value: Float
        get() = current
        set(value) {
            current = value
            onValueChanged(value)
        }
}
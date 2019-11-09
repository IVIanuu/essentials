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

import androidx.compose.Ambient
import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun MultiAmbientProvider(
    vararg pairs: AmbientWithValue<*>,
    children: @Composable() () -> Unit
) = composable("MultiAmbientProvider") {
    pairs
        .map { pair ->
            { children: @Composable() () -> Unit ->
                pair.Provider(children)
            }
        }
        .fold(children) { current, ambient ->
            { ambient(current) }
        }.invoke()
}

// todo is this a good name?
infix fun <T> Ambient<T>.with(value: T): AmbientWithValue<T> = AmbientWithValue(this, value)

data class AmbientWithValue<T>(
    val ambient: Ambient<T>,
    val value: T
) {
    @Composable
    fun Provider(
        children: @Composable() () -> Unit
    ) = composable("AmbientWithValueProvider") {
        ambient.Provider(value = value, children = children)
    }
}
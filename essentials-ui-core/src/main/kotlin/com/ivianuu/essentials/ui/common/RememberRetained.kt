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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.staticCompositionLocalOf
import com.ivianuu.injekt.common.Scope

val LocalRetainedScope = staticCompositionLocalOf<Scope> {
    error("No retained scope specified")
}

// todo remove once compose is fixed
@Composable
inline fun <T : Any> rememberRetained(
    key: Any = currentComposer.compoundKeyHash,
    noinline init: () -> T
): T = rememberRetained(inputs = *emptyArray(), key = key, init = init)

@Composable
inline fun <T : Any> rememberRetained(
    vararg inputs: Any?,
    key: Any = currentComposer.compoundKeyHash,
    noinline init: () -> T
): T {
    val finalKey = key.hashCode()
    val scope = LocalRetainedScope.current
    return synchronized(scope) {
        var value: ValueWithInputs<T>? = scope.get(key = finalKey)
        if (value != null && !value.inputs.contentEquals(inputs)) {
            scope.minusAssign(key.hashCode())
            value = null
        }

        if (value == null) {
            value = ValueWithInputs(init(), inputs)
            scope[finalKey] = value
        }
        value.value
    }
}

@PublishedApi
internal class ValueWithInputs<T>(
    val value: T,
    val inputs: Array<out Any?>
) : Scope.Disposable {
    override fun dispose() {
        (value as? Scope.Disposable)?.dispose()
    }
}

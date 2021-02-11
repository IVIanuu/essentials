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

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContextAccessor
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.rememberCompositionContext

@Composable
val <T : Any> CompositionLocal<T>.currentOrNull: T?
    get() {
        val compositionContext = rememberCompositionContext()
        val compositionLocals =
            CompositionContextAccessor.getCompositionLocalScope(compositionContext)
        return compositionLocals[this as CompositionLocal<Any?>]?.value as? T
    }

@Composable
val CompositionLocal<*>.hasCurrentValue: Boolean get() {
    val compositionContext = rememberCompositionContext()
    return this in CompositionContextAccessor.getCompositionLocalScope(compositionContext)
}

@Composable
inline fun <T : Any> CompositionLocal<T>.currentOrElse(defaultValue: () -> T): T =
    currentOrNull ?: defaultValue()

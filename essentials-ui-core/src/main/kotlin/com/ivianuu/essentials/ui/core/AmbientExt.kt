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

package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.Ambient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionReferenceAccessor
import androidx.compose.runtime.compositionReference

@Composable
val <T : Any> Ambient<T>.currentOrNull: T?
    get() {
        val ref = compositionReference()
        val ambients = CompositionReferenceAccessor.getAmbientScope(ref)
        return ambients[this as Ambient<Any?>]?.value as? T
    }

@Composable
val Ambient<*>.hasCurrentValue: Boolean get() {
    val ref = compositionReference()
    return this in CompositionReferenceAccessor.getAmbientScope(ref)
}

@Composable
inline fun <T : Any> Ambient<T>.currentOrElse(defaultValue: () -> T): T =
    currentOrNull ?: defaultValue()

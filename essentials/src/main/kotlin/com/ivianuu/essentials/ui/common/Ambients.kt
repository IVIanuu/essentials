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

package com.ivianuu.essentials.ui.common

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.remember
import com.ivianuu.essentials.composehelpers.current

interface Updateable<T> {
    fun updateFrom(other: T)
}

@Composable
fun <T : Updateable<T>> Ambient<T>.UpdateProvider(
    value: T,
    children: @Composable() () -> Unit
) {
    val finalValue = remember { value }
    finalValue.updateFrom(value)
    Provider(value = finalValue, children = children)
}

interface Mergeable<T> {
    fun merge(other: T): T
}

@Composable
fun <T : Mergeable<T>> Ambient<T>.MergeProvider(
    value: T,
    children: @Composable() () -> Unit
) {
    val currentValue = this.current
    val newValue = currentValue.merge(value)
    Provider(value = newValue, children = children)
}

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

package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy

@Composable
inline fun <T> rememberUntrackedState(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    crossinline init: () -> T,
): MutableState<T> = remember { untrackedStateOf(init(), policy) }

@Composable
inline fun <T> rememberUntrackedState(
    vararg inputs: Any?,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    crossinline init: () -> T,
): MutableState<T> = remember(*inputs) {
    untrackedStateOf(init(), policy)
}

fun <T> untrackedStateOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()
): MutableState<T> = UntrackedState(value, policy)

private class UntrackedState<T>(
    value: T,
    val policy: SnapshotMutationPolicy<T>
) : MutableState<T> {
    override var value: T = value
        get() = synchronized(this) { field }
        set(value) {
            val oldValue = synchronized(this) { field }
            if (!policy.equivalent(oldValue, value)) {
                synchronized(this) { field = value }
            }
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

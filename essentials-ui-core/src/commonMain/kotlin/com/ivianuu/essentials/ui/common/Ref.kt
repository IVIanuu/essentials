/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.Stable
import androidx.compose.runtime.structuralEqualityPolicy
import kotlin.reflect.KProperty

fun <T> refOf(
  value: T,
  policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()
): Ref<T> = RefImpl(value, policy)

@Stable interface Ref<T> {
  var value: T
}

fun <T> Ref<T>.component1(): T = value
fun <T> Ref<T>.component2(): (T) -> Unit = { value = it }

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Ref<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Ref<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
  this.value = value
}

private class RefImpl<T>(
  value: T,
  val policy: SnapshotMutationPolicy<T>
) : Ref<T> {
  override var value: T = value
    get() = synchronized(this) { field }
    set(value) {
      val oldValue = synchronized(this) { field }
      if (!policy.equivalent(oldValue, value)) {
        synchronized(this) { field = value }
      }
    }
}

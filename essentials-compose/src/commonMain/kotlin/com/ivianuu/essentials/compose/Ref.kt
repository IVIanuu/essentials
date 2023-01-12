/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.Stable
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
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
  private val _value = atomic(value)
  override var value: T
    get() = _value.value
    set(value) {
      _value.loop { oldValue ->
        if (policy.equivalent(oldValue, value) || _value.compareAndSet(oldValue, value))
          return
      }
    }
}

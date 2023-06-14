/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.Stable
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.reflect.KProperty

fun <T> refOf(value: T): Ref<T> = RefImpl(value)

@Stable interface Ref<T> {
  var value: T
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Ref<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Ref<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
  this.value = value
}

private class RefImpl<T>(value: T) : Ref<T> {
  private val _value = atomic(value)
  override var value: T
    get() = _value.value
    set(value) {
      _value.loop { oldValue ->
        if (_value.compareAndSet(oldValue, value))
          return
      }
    }
}

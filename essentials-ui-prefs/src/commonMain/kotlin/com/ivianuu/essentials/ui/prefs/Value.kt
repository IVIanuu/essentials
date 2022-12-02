/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.optics.Lens
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.bind

interface Value<T> {
  val current: T
  val updater: (T) -> Unit

  companion object {
    inline operator fun <T> invoke(
      crossinline current: () -> T,
      crossinline updater: (T) -> Unit
    ): Value<T> = object : Value<T>, (T) -> Unit {
      override val current: T
        get() = current()
      override val updater: (T) -> Unit
        get() = this

      override fun invoke(p1: T) {
        updater(p1)
      }
    }

    inline operator fun <T> invoke(
      current: T,
      crossinline updater: (T) -> Unit
    ) = Value<T>({ current }, updater)
  }
}

@Composable fun <S, T> DataStore<S>.value(lens: Lens<S, T>, initial: T): Value<T> {
  val current = data.bind { null }?.let { lens.get(it) } ?: initial
  return Value(current = current, updater = action { newValue ->
    updateData { lens.set(this, newValue) }
  })
}

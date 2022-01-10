/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.*

inline fun <R> nullable(block: (@Inject NullableControl) -> R): R? = try {
  block(NullableControlImpl)
} catch (e: NullableControlImpl.ShortCircuitException) {
  null
}

interface NullableControl {
  fun <T> bind(x: T?): T
}

inline fun <T> T.bind(@Inject control: NullableControl): T = control.bind(this)

@PublishedApi internal object NullableControlImpl : NullableControl {
  override fun <T> bind(x: T?): T = x ?: throw ShortCircuitException

  object ShortCircuitException : ControlException()
}

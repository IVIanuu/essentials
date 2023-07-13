/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.Provide

interface Lerper<T> {
  fun lerp(start: T, stop: T, fraction: Float): T
  fun unlerp(start: T, stop: T, value: T): Float

  @Provide companion object {
    inline operator fun <T> invoke(
      crossinline lerp: (T, T, Float) -> T,
      crossinline unlerp: (T, T, T) -> Float
    ): Lerper<T> = object : Lerper<T> {
      override fun lerp(start: T, stop: T, fraction: Float): T = lerp.invoke(start, stop, fraction)
      override fun unlerp(start: T, stop: T, value: T): Float = unlerp.invoke(start, stop, value)
    }

    @Provide val float = Lerper<Float>(
      lerp = { start, stop, fraction ->
        start * (1f - fraction) + stop * fraction
      },
      unlerp = { start, stop, value ->
        (if (stop - start == 0f) 0f else (value - start) / (stop - start)).coerceIn(0f, 1f)
      }
    )

    context(Lerper<Float>) @Provide val double: Lerper<Double> get() = Lerper(
      lerp = { start, stop, fraction ->
        start * (1.0 - fraction) + stop * fraction
      },
      unlerp = { start, stop, value ->
        unlerp(start.toFloat(), stop.toFloat(), value.toFloat())
      }
    )

    context(Lerper<Float>) @Provide val int: Lerper<Int> get() = Lerper(
      lerp = { start, stop, fraction ->
        (start * (1f - fraction) + stop * fraction).toInt()
      },
      unlerp = { start, stop, value ->
        unlerp(start.toFloat(), stop.toFloat(), value.toFloat())
      }
    )

    context(Lerper<Float>) @Provide val long: Lerper<Long> get() = Lerper(
      lerp = { start, stop, fraction ->
        (start * (1f - fraction) + stop * fraction).toLong()
      },
      unlerp = { start, stop, value ->
        unlerp(start.toFloat(), stop.toFloat(), value.toFloat())
      }
    )
  }
}

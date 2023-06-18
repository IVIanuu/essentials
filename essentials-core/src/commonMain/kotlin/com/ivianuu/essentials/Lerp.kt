/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

interface Lerper<T> {
  fun lerp(start: T, stop: T, fraction: Float): T
  fun unlerp(start: T, stop: T, value: T): Float

  companion object {
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

    @Provide val double = Lerper<Double>(
      lerp = { start, stop, fraction ->
        start * (1.0 - fraction) + stop * fraction
      },
      unlerp = { start, stop, value -> unlerp(start.toFloat(), stop.toFloat(), value.toFloat()) }
    )

    @Provide val int = Lerper<Int>(
      lerp = { start, stop, fraction ->
        (start * (1f - fraction) + stop * fraction).toInt()
      },
      unlerp = { start, stop, value ->
        unlerp(start.toFloat(), stop.toFloat(), value.toFloat())
      }
    )

    @Provide val long = Lerper<Long>(
      lerp = { start, stop, fraction ->
        (start * (1f - fraction) + stop * fraction).toLong()
      },
      unlerp = { start, stop, value ->
        unlerp(start.toFloat(), stop.toFloat(), value.toFloat())
      }
    )
  }
}

fun <T> lerp(start: T, stop: T, fraction: Float, @Inject lerper: Lerper<T>): T =
  lerper.lerp(start, stop, fraction)

fun <T> unlerp(start: T, stop: T, value: T, @Inject lerper: Lerper<T>): Float =
  lerper.unlerp(start, stop, value)

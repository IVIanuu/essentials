/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.*
import kotlin.math.*
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

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

    @Provide val double = Lerper<Double>(
      lerp = { start, stop, fraction ->
        start * (1.0 - fraction) + stop * fraction
      },
      unlerp = { start, stop, value ->
        (if (stop - start == 0.0) 0.0 else (value - start) / (stop - start)).coerceIn(0.0, 1.0)
          .toFloat()
      }
    )

    @Provide val duration = Lerper<Duration>(
      lerp = { start, stop, fraction ->
        lerp(start.inWholeMilliseconds, stop.inWholeMilliseconds, fraction)
          .milliseconds
      },
      unlerp = { start, stop, value ->
        unlerp(start.inWholeMilliseconds, stop.inWholeMilliseconds, value.inWholeMilliseconds)
      }
    )

    @Provide val int = Lerper<Int>(
      lerp = { start, stop, fraction ->
        (start * (1f - fraction) + stop * fraction).roundToInt()
      },
      unlerp = { start, stop, value ->
        unlerp(start.toFloat(), stop.toFloat(), value.toFloat())
      }
    )

    @Provide val long = Lerper<Long>(
      lerp = { start, stop, fraction ->
        (start * (1f - fraction) + stop * fraction).roundToLong()
      },
      unlerp = { start, stop, value ->
        unlerp(start.toDouble(), stop.toDouble(), value.toDouble())
      }
    )
  }
}

fun <T> lerp(start: T, stop: T, fraction: Float, lerper: Lerper<T> = inject): T =
  lerper.lerp(start, stop, fraction)

fun <T> unlerp(start: T, stop: T, value: T, lerper: Lerper<T> = inject): Float =
  lerper.unlerp(start, stop, value)

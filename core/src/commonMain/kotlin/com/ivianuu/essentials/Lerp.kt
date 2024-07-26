/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.math.*
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

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
  }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float =
  start * (1f - fraction) + stop * fraction

fun unlerp(start: Float, stop: Float, value: Float): Float =
  (if (stop - start == 0f) 0f else (value - start) / (stop - start)).coerceIn(0f, 1f)

@Provide val FloatLerper = Lerper<Float>(
  lerp = { start, stop, fraction -> lerp(start, stop, fraction) },
  unlerp = { start, stop, value -> unlerp(start, stop, value) }
)

fun lerp(start: Double, stop: Double, fraction: Float): Double =
  start * (1.0 - fraction) + stop * fraction

fun unlerp(start: Double, stop: Double, value: Double): Float =
  (if (stop - start == 0.0) 0.0 else (value - start) / (stop - start)).coerceIn(0.0, 1.0)
    .toFloat()

@Provide val DoubleLerper = Lerper<Double>(
  lerp = { start, stop, fraction -> lerp(start, stop, fraction) },
  unlerp = { start, stop, value -> unlerp(start, stop, value) }
)

fun lerp(start: Int, stop: Int, fraction: Float): Int =
  (start * (1f - fraction) + stop * fraction).roundToInt()

fun unlerp(start: Int, stop: Int, value: Int): Float =
  unlerp(start.toFloat(), stop.toFloat(), value.toFloat())

@Provide val IntLerper = Lerper<Int>(
  lerp = { start, stop, fraction -> lerp(start, stop, fraction) },
  unlerp = { start, stop, value -> unlerp(start, stop, value) }
)

fun lerp(start: Long, stop: Long, fraction: Float): Long =
  (start * (1f - fraction) + stop * fraction).roundToLong()

fun unlerp(start: Long, stop: Long, value: Long): Float =
  unlerp(start.toDouble(), stop.toDouble(), value.toDouble())

@Provide val LongLerper = Lerper<Long>(
  lerp = { start, stop, fraction -> lerp(start, stop, fraction) },
  unlerp = { start, stop, value -> unlerp(start, stop, value) }
)

fun lerp(start: Duration, stop: Duration, fraction: Float): Duration =
  lerp(start.inWholeNanoseconds, stop.inWholeNanoseconds, fraction).nanoseconds

fun unlerp(start: Duration, stop: Duration, value: Duration): Float =
  unlerp(start.inWholeNanoseconds, stop.inWholeNanoseconds, value.inWholeNanoseconds)

@Provide val DurationLerper = Lerper<Duration>(
  lerp = { start, stop, fraction -> lerp(start, stop, fraction) },
  unlerp = { start, stop, value -> unlerp(start, stop, value) }
)

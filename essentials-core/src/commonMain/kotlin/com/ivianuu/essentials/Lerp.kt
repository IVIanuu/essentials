/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

fun lerp(start: Float, stop: Float, fraction: Float): Float =
  start * (1f - fraction) + stop * fraction

fun lerp(start: Double, stop: Double, fraction: Float): Double =
  start * (1f - fraction) + stop * fraction

fun lerp(start: Int, stop: Int, fraction: Float): Int =
  (start * (1f - fraction) + stop * fraction).toInt()

fun lerp(start: Long, stop: Long, fraction: Float): Long =
  (start * (1f - fraction) + stop * fraction).toLong()

fun unlerp(start: Float, stop: Float, value: Float): Float =
  (if (stop - start == 0f) 0f else (value - start) / (stop - start)).coerceIn(0f, 1f)

fun unlerp(start: Double, stop: Double, value: Double): Float =
  unlerp(start.toFloat(), stop.toFloat(), value.toFloat())

fun unlerp(start: Int, stop: Int, value: Int): Float =
  unlerp(start.toFloat(), stop.toFloat(), value.toFloat())

fun unlerp(start: Long, stop: Long, value: Long): Float =
  unlerp(start.toFloat(), stop.toFloat(), value.toFloat())

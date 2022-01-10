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

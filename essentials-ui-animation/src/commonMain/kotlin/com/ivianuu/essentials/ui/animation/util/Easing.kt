/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import com.ivianuu.essentials.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

fun interval(
  start: Float,
  end: Float,
  fraction: Float
): Float = ((fraction - start) / (end - start)).coerceIn(0f, 1f)

fun arcLerp(
  start: Offset,
  end: Offset,
  fraction: Float
): Offset {
  if (fraction == 0f) return start
  if (fraction == 1f) return end
  val delta = end - start
  val deltaX = abs(delta.x)
  val deltaY = abs(delta.y)
  val distanceFromAtoB = delta.getDistance()
  val c = Offset(end.x, start.y)

  if (deltaX < 2f || deltaY < 2f)
    return lerp(start, end, fraction)

  val radius: Float
  val beginAngle: Float
  val endAngle: Float
  val center: Offset
  if (deltaX < deltaY) {
    radius = distanceFromAtoB * distanceFromAtoB / (c - start).getDistance() / 2f
    center = Offset(end.x + radius * (start.x - end.x).sign, end.y)
    val sweepAngle = 2f * asin(distanceFromAtoB / (2f * radius))
    if (start.x < end.x) {
      beginAngle = sweepAngle * (start.y - end.y).sign
      endAngle = 0f
    } else {
      beginAngle = PI.toFloat() + sweepAngle * (end.y - start.y).sign
      endAngle = PI.toFloat()
    }
  } else {
    radius = distanceFromAtoB * distanceFromAtoB / (c - end).getDistance() / 2f
    center = Offset(start.x, start.y + (end.y - start.y).sign * radius)
    val sweepAngle = 2f * asin(distanceFromAtoB / (2f * radius))
    if (start.y < end.y) {
      beginAngle = -PI.toFloat() / 2f
      endAngle = beginAngle + sweepAngle * (end.x - start.x).sign
    } else {
      beginAngle = PI.toFloat() / 2f
      endAngle = beginAngle + sweepAngle * (start.x - end.x).sign
    }
  }

  val angle = lerp(beginAngle, endAngle, fraction)
  val x = cos(angle) * radius
  val y = sin(angle) * radius
  return center + Offset(x, y)
}

/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

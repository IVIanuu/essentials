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

package com.ivianuu.essentials

fun lerp(start: Float, stop: Float, fraction: Float): Float =
  start * (1f - fraction) + stop * fraction

fun lerp(start: Double, stop: Double, fraction: Float): Double =
  start * (1f - fraction) + stop * fraction

fun lerp(start: Int, stop: Int, fraction: Float): Int =
  (start * (1f - fraction) + stop * fraction).toInt()

fun lerp(start: Long, stop: Long, fraction: Float): Long =
  (start * (1f - fraction) + stop * fraction).toLong()

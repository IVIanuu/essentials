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

package com.ivianuu.essentials.billing

fun String.toIso8601Duration(): Iso8601Duration = Iso8601Duration(
  amount = drop(1).dropLast(1).toInt(),
  unit = when (get(lastIndex)) {
    'Y' -> Iso8601Duration.Unit.YEAR
    'M' -> Iso8601Duration.Unit.MONTH
    'W' -> Iso8601Duration.Unit.WEEK
    else -> throw IllegalArgumentException("Unexpected iso string $this")
  }
)

data class Iso8601Duration(val amount: Int, val unit: Unit) {
  enum class Unit {
    YEAR, MONTH, WEEK
  }
}

fun Iso8601Duration.toReadableString() = "$amount " + when (unit) {
  Iso8601Duration.Unit.YEAR -> "year"
  Iso8601Duration.Unit.MONTH -> "month"
  Iso8601Duration.Unit.WEEK -> "week"
} + " ${if (amount > 1) "s" else ""}"

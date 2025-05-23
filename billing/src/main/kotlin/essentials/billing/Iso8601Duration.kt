/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.billing

fun String.toIso8601Duration(): Iso8601Duration = Iso8601Duration(
  amount = drop(1).dropLast(1).toInt(),
  unit = when (get(lastIndex)) {
    'Y' -> Iso8601Duration.Unit.YEAR
    'M' -> Iso8601Duration.Unit.MONTH
    'W' -> Iso8601Duration.Unit.WEEK
    'D' -> Iso8601Duration.Unit.DAY
    else -> throw IllegalArgumentException("Unexpected iso string $this")
  }
)

data class Iso8601Duration(val amount: Int, val unit: Unit) {
  enum class Unit { YEAR, MONTH, WEEK, DAY }
}

fun Iso8601Duration.toReadableString() = "$amount " + when (unit) {
  Iso8601Duration.Unit.YEAR -> "year"
  Iso8601Duration.Unit.MONTH -> "month"
  Iso8601Duration.Unit.WEEK -> "week"
  Iso8601Duration.Unit.DAY -> "day"
} + " ${if (amount > 1) "'s" else ""}"

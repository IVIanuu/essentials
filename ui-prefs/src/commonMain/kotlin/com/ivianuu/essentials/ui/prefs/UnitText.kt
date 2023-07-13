/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import kotlin.math.roundToInt

context(UnitStrings) @Composable fun UnitText(value: Any, unit: ValueUnit) {
  Text(unit.picker(this@UnitStrings, value))
}

context(UnitStrings) @Composable fun ScaledPercentageUnitText(value: Float) {
  UnitText((value * 100f).roundToInt(), ValueUnit.PERCENTAGE)
}

enum class ValueUnit(val picker: context(UnitStrings) (Any) -> String) {
  DP({ dp(it.cast()) }),
  MILLIS({ millis(it.cast()) }),
  PERCENTAGE({ percentage(it.cast()) }),
  PX({ px(it.cast()) }),
  SECONDS({ seconds(it.cast()) })
}

interface UnitStrings {
  fun dp(value: Int): String
  fun millis(value: Long): String
  fun percentage(value: Int): String
  fun px(value: Int): String
  fun seconds(value: Int): String

  @Provide object Impl : UnitStrings {
    override fun dp(value: Int) = "$value dp"
    override fun millis(value: Long) = "$value ms"
    override fun percentage(value: Int) = "$value%"
    override fun px(value: Int) = "$value px"
    override fun seconds(value: Int) = "$value seconds"
  }
}

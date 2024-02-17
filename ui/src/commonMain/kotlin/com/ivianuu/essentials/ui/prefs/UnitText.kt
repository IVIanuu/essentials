/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.material.*
import androidx.compose.runtime.*
import kotlin.math.*

@Composable fun UnitText(value: Any, unit: ValueUnit) {
  Text(unit.format(value))
}

@Composable fun ScaledPercentageUnitText(value: Float) {
  UnitText((value * 100f).roundToInt(), ValueUnit.PERCENTAGE)
}

enum class ValueUnit(val format: (Any) -> String) {
  DP({ "$it dp" }),
  MILLIS({ "$it ms" }),
  PERCENTAGE({ "$it%" }),
  PX({ "$it px" }),
  SECONDS({ "$it seconds" })
}

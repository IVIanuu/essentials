/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.prefs

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlin.math.roundToInt

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

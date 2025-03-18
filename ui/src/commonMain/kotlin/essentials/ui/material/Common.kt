package essentials.ui.material

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlin.math.*

@Composable fun ColorIcon(color: Color) {
  Surface(
    modifier = Modifier.requiredSize(48.dp),
    color = color,
    shape = CircleShape,
    border = BorderStroke(
      width = 1.dp,
      color = LocalContentColor.current
    )
  ) {}
}


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


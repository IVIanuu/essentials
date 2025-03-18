package essentials.ui.material

import androidx.annotation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.*

@Composable fun containerColor(selected: Boolean, focused: Boolean, tone: Tone): Color {
  return if (selected) {
    MaterialTheme.colorScheme.tertiary
  } else if (focused) {
    MaterialTheme.colorScheme.surfaceContainerHigh
  } else {
    containerColorForTone(tone)
  }
}

@Composable fun iconColor(tone: Tone, selected: Boolean): Color = if (selected) {
  MaterialTheme.colorScheme.surface
} else {
  iconColorForTone(tone)
}

@Composable fun shape(orientation: SectionOrientation, first: Boolean, last: Boolean): RoundedCornerShape {
  return when (orientation) {
    SectionOrientation.VERTICAL -> firstLastCornersVertical(first, last)
    SectionOrientation.HORIZONTAL -> firstLastCornersHorizontal(first, last)
  }
}

enum class SectionOrientation { VERTICAL, HORIZONTAL }

@Composable fun sectionTextColorForTone(selected: Boolean, tone: Tone): Color {
  return if (selected) {
    MaterialTheme.colorScheme.surface
  } else {
    textColorForTone(tone)
  }
}

@Composable fun firstLastCornersVertical(
  first: Boolean = false,
  last: Boolean = false,
  defaultCornerRadius: Dp = 8.dp,
  pronouncedCornerRadius: Dp = 28.dp,
): RoundedCornerShape {
  val topCorners = if (first) pronouncedCornerRadius else defaultCornerRadius
  val bottomCorners = if (last) pronouncedCornerRadius else defaultCornerRadius
  return RoundedCornerShape(
    topStart = topCorners,
    topEnd = topCorners,
    bottomStart = bottomCorners,
    bottomEnd = bottomCorners
  )
}

fun firstLastCornersHorizontal(
  first: Boolean = false,
  last: Boolean = false,
  defaultCornerRadius: Dp =  9.dp,
  pronouncedCornerRadius: Dp = 32.dp,
): RoundedCornerShape {
  val startCorners = if (first) pronouncedCornerRadius else defaultCornerRadius
  val endCorners = if (last) pronouncedCornerRadius else defaultCornerRadius
  return RoundedCornerShape(
    topStart = startCorners,
    topEnd = endCorners,
    bottomStart = startCorners,
    bottomEnd = endCorners
  )
}

enum class Tone { NEGATIVE, WARNING, NEUTRAL, POSITIVE }

val baseWarningColor = Color(0xffffb624)

@Composable fun textWarningColor() = MaterialTheme.colorScheme.onSurfaceVariant.blend(
  to = baseWarningColor,
  by = 0.2f
)

@Composable fun textErrorColor() = MaterialTheme.colorScheme.onSurfaceVariant.blend(
  to = MaterialTheme.colorScheme.error,
  by = 0.5f
)

@Composable fun vividTextColorForTone(tone: Tone) = when (tone) {
  Tone.POSITIVE -> MaterialTheme.colorScheme.surface
  Tone.NEUTRAL -> MaterialTheme.colorScheme.primary
  Tone.NEGATIVE -> MaterialTheme.colorScheme.surface
  Tone.WARNING -> MaterialTheme.colorScheme.error
}

@Composable fun vividContainerColorForTone(tone: Tone) = when (tone) {
  Tone.POSITIVE -> MaterialTheme.colorScheme.primary
  Tone.NEUTRAL -> MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
  Tone.NEGATIVE -> textErrorColor()
  Tone.WARNING -> MaterialTheme.colorScheme.errorContainer
}

@Composable fun containerColorForTone(tone: Tone) = when (tone) {
  Tone.POSITIVE -> MaterialTheme.colorScheme.primary
  Tone.NEUTRAL -> MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp)
  Tone.NEGATIVE -> MaterialTheme.colorScheme.surface.blend(
    to = Color.Red,
    by = 0.13f
  )
  Tone.WARNING -> MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp).blend(
    to = baseWarningColor,
    by = 0.13f
  )
}


@Composable fun textColorForTone(tone: Tone) = when (tone) {
  Tone.POSITIVE -> MaterialTheme.colorScheme.surface
  Tone.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
  Tone.NEGATIVE -> textErrorColor()
  Tone.WARNING -> textWarningColor()
}

@Composable fun iconColorForTone(tone: Tone) = when (tone) {
  Tone.POSITIVE -> MaterialTheme.colorScheme.surface
  Tone.NEUTRAL -> MaterialTheme.colorScheme.tertiary
  Tone.NEGATIVE -> textErrorColor()
  Tone.WARNING -> textWarningColor()
}

fun Color.blend(to: Color, @FloatRange(0.0, 1.0) by: Float): Color {
  return Color(ColorUtils.blendARGB(this.toArgb(), to.toArgb(), by))
}

package essentials.ui.material

import androidx.annotation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.*

@Composable fun Section(
  modifier: Modifier = Modifier,
  first: Boolean = false,
  last: Boolean = false,
  orientation: SectionOrientation = SectionOrientation.VERTICAL,
  tone: Tone = Tone.NEUTRAL,
  focused: Boolean = false,
  selected: Boolean = false,
  margin: PaddingValues = SectionDefaults.marginValues(orientation, last),
  padding: PaddingValues = SectionDefaults.paddingValues(orientation, first, last),
  shape: RoundedCornerShape = SectionDefaults.shape(orientation, first, last),
  containerColor: Color = SectionDefaults.containerColor(selected, focused, tone),
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  val extraInternalPadding by animateDpAsState(
    if (selected) 8.dp else 0.dp, label = "extraInternalPadding"
  )

  Column(
    modifier = Modifier
      .clip(shape)
      .padding(margin)
      .clip(shape)
      .combinedClickable(
        enabled = onClick != null || onLongClick != null,
        onClick = { onClick?.invoke() },
        onLongClick = { onLongClick?.invoke() }
      )
      .focusable(onClick != null)
      .background(containerColor)
      .clip(shape)
      .then(modifier)
      .padding(padding)
      .padding(
        when (orientation) {
          SectionOrientation.VERTICAL -> PaddingValues(vertical = extraInternalPadding)
          SectionOrientation.HORIZONTAL -> PaddingValues(horizontal = extraInternalPadding)
        }
      )
  ) {
    content()
  }
}


object SectionDefaults {
  fun paddingValues(orientation: SectionOrientation, first: Boolean, last: Boolean): PaddingValues =
    when (orientation) {
      SectionOrientation.VERTICAL -> verticalPaddingValues(first, last)
      SectionOrientation.HORIZONTAL -> horizontalPaddingValues(first, last)
    }

  fun verticalPaddingValues(first: Boolean = false, last: Boolean = false) = PaddingValues(
    top = if (first) 10.dp else 8.dp,
    bottom = if (last) 10.dp else 8.dp,
    start = 22.dp,
    end = 22.dp
  )

  fun horizontalPaddingValues(first: Boolean = false, last: Boolean = false) = PaddingValues(
    start = if (first) 24.dp else 20.dp,
    end = if (last) 24.dp else 20.dp,
    top = 12.dp,
    bottom = 12.dp,
  )

  fun marginValues(orientation: SectionOrientation, last: Boolean): PaddingValues = when (orientation) {
    SectionOrientation.VERTICAL -> verticalMarginValues(last)
    SectionOrientation.HORIZONTAL -> horizontalMarginValues()
  }

  fun verticalMarginValues(last: Boolean = false) =
    PaddingValues(bottom = if (last) 16.dp else 2.dp, top = 2.dp)

  fun horizontalMarginValues() = PaddingValues(end = 5.dp, start = 0.dp)

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

fun firstLastCornersChip(
  first: Boolean = false,
  last: Boolean = false,
  defaultCornerRadius: Dp = 5.dp,
  pronouncedCornerRadius: Dp = 22.dp,
) = firstLastCornersHorizontal(
  first = first,
  last = last,
  defaultCornerRadius = defaultCornerRadius,
  pronouncedCornerRadius = pronouncedCornerRadius
)

enum class Tone { NEGATIVE, WARNING, NEUTRAL, POSITIVE, }

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


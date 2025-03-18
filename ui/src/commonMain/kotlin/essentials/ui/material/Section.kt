package essentials.ui.material

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.common.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

object SectionDefaults {
  @Composable fun colors(
    selected: Boolean = false,
    focused: Boolean = false,
    tone: Tone = Tone.NEUTRAL
  ) = SectionColors(
    containerColor = containerColor(selected, focused, tone),
    contentColor = sectionTextColorForTone(selected, tone),
    iconColor = iconColor(tone, selected)
  )

  @Composable fun colors(
    containerColor: Color,
    contentColor: Color = guessingContentColorFor(containerColor),
    iconColor: Color = MaterialTheme.colorScheme.tertiary
  ) = SectionColors(containerColor, contentColor, iconColor)

  @Composable fun shape(sectionType: SectionType): Shape {
    val topCorners = if (sectionType.first) 28.dp else 8.dp
    val bottomCorners = if (sectionType.last) 28.dp else 8.dp
    return RoundedCornerShape(
      topStart = topCorners,
      topEnd = topCorners,
      bottomStart = bottomCorners,
      bottomEnd = bottomCorners
    )
  }

  @Composable fun padding(sectionType: SectionType, selected: Boolean) = SectionPadding(
    padding = PaddingValues(
      start = 16.dp,
      end = 16.dp,
      top = if (sectionType.first) 16.dp else 2.dp,
      bottom = if (sectionType.last) 16.dp else 2.dp
    ),
    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    extraContentPadding = {
      PaddingValues(
        vertical = animateDpAsState(if (selected) 8.dp else 0.dp).value
      )
    }
  )
}

data class SectionColors(val containerColor: Color, val contentColor: Color, val iconColor: Color)

data class SectionPadding(
  val padding: PaddingValues,
  val contentPadding: PaddingValues,
  val extraContentPadding: @Composable () -> PaddingValues
)

@Composable fun SectionContainer(
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  selected: Boolean = false,
  focused: Boolean = false,
  colors: SectionColors = SectionDefaults.colors(selected, focused),
  shape: Shape = SectionDefaults.shape(sectionType),
  padding: SectionPadding = SectionDefaults.padding(sectionType, selected),
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable () -> Unit
) {
  val currentContainerColor by animateColorAsState(colors.containerColor)
  val currentContentColor by animateColorAsState(colors.contentColor)
  Surface(
    color = currentContainerColor,
    contentColor = currentContentColor,
    modifier = modifier.padding(padding.padding),
    shape = shape
  ) {
    Box(
      modifier = Modifier
        .then(
          if (onClick == null) Modifier
          else Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
            interactionSource = interactionSource,
            indication = ripple()
          )
        )
        .padding(padding.contentPadding)
        .padding(padding.extraContentPadding())
    ) { content() }
  }
}

enum class SectionType(val first: Boolean, val last: Boolean) {
  FIRST(true, false), LAST(false, true), MIDDLE(false, false), SINGLE(true, true)
}

fun sectionTypeOf(index: Int, itemCount: Int) = when {
  index == 0 -> if (itemCount == 1) SectionType.SINGLE else SectionType.FIRST
  index == itemCount - 1 -> SectionType.LAST
  else -> SectionType.MIDDLE
}

@Composable fun SectionAlert(
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  selected: Boolean = false,
  focused: Boolean = false,
  colors: SectionColors = SectionDefaults.colors(selected, focused),
  shape: Shape = SectionDefaults.shape(sectionType),
  padding: SectionPadding = SectionDefaults.padding(sectionType, selected),
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  title: (@Composable () -> Unit)? = null,
  icon: (@Composable () -> Unit)? = null,
  actions: (@Composable RowScope.() -> Unit)? = null,
  description: @Composable (PaddingValues) -> Unit
) {
  SectionContainer(
    modifier = modifier,
    sectionType = sectionType,
    selected = selected,
    focused = focused,
    colors = colors,
    shape = shape,
    padding = padding,
    onClick = onClick,
    onLongClick = onLongClick,
    interactionSource = interactionSource
  ) {
    Column {
      if (title != null || icon != null) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
          if (title != null)
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) { title() }

          Spacer(Modifier.weight(1f))

          if (icon != null)
            CompositionLocalProvider(LocalContentColor provides colors.iconColor) {
              icon()
            }
        }
      }

      ProvideContentColorTextStyle(
        LocalContentColor.current.copy(alpha = ContentAlpha.Medium),
        MaterialTheme.typography.bodyMedium
      ) {
        description(PaddingValues(
          top = if (title != null) 8.dp else 0.dp,
          bottom = if (actions != null) 8.dp else 0.dp
        ))
      }

      if (actions != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) { actions() }
      }
    }
  }
}

@Composable fun SectionListItem(
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  selected: Boolean = false,
  focused: Boolean = false,
  colors: SectionColors = SectionDefaults.colors(selected, focused),
  shape: Shape = SectionDefaults.shape(sectionType),
  padding: SectionPadding = SectionDefaults.padding(sectionType, selected),
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  title: (@Composable () -> Unit)? = null,
  description: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null
) {
  SectionContainer(
    modifier = modifier,
    sectionType = sectionType,
    selected = selected,
    focused = focused,
    colors = colors,
    shape = shape,
    padding = padding,
    onClick = onClick,
    onLongClick = onLongClick,
    interactionSource = interactionSource
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (leading != null)
        ProvideContentColorTextStyle(
          contentColor = colors.iconColor,
          textStyle = MaterialTheme.typography.labelMedium
        ) { leading() }

      Column(
        modifier = Modifier
          .weight(1f)
          .align(Alignment.CenterVertically),
        verticalArrangement = Arrangement.Center
      ) {
        if (title != null)
          ProvideContentColorTextStyle(
            contentColor = colors.contentColor,
            textStyle = MaterialTheme.typography.bodyLarge
          ) { title() }
        if (description != null)
          ProvideContentColorTextStyle(
            contentColor = colors.contentColor.copy(alpha = ContentAlpha.Medium),
            textStyle = MaterialTheme.typography.bodyMedium
          ) { description() }
      }

      if (trailing != null)
        ProvideContentColorTextStyle(
          contentColor = colors.iconColor,
          textStyle = MaterialTheme.typography.labelMedium
        ) { trailing() }
    }
  }
}

@Composable fun SectionSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  colors: SectionColors = SectionDefaults.colors(false),
  shape: Shape = SectionDefaults.shape(sectionType),
  padding: SectionPadding = SectionDefaults.padding(sectionType, false),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  title: (@Composable () -> Unit)? = null,
  description: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null
) {
  SectionListItem(
    modifier = modifier,
    sectionType = sectionType,
    colors = colors,
    shape = shape,
    padding = padding,
    interactionSource = interactionSource,

    onClick = { onCheckedChange(!checked) },
    title = title,
    description = description,
    leading = leading,
    trailing = {
      Switch(
        checked = checked,
        onCheckedChange = null,
        interactionSource = interactionSource
      )
    }
  )
}

@Composable fun <T : Comparable<T>> SectionSlider(
  value: T,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,

  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  stepPolicy: StepPolicy<T> = NoStepsStepPolicy,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: @Composable ((T) -> Unit)? = { Text(it.toString()) },
  lerper: Lerper<T> = inject,
  valueRange: @DefaultSliderRange ClosedRange<T> = inject,
) {
  var internalValue: T? by remember { mutableStateOf(null) }
  var internalValueEraseJob: Job? by remember { mutableStateOf(null) }

  SectionListItem(
    modifier = modifier,
    sectionType = sectionType,
    title = headlineContent,
    description = {
      @Provide val scope = rememberCoroutineScope()
      Row(verticalAlignment = Alignment.CenterVertically) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
          EsSlider(
            modifier = Modifier
              .padding(end = 16.dp)
              .weight(1f),
            value = internalValue ?: value,
            onValueChange = { newValue ->
              internalValue = newValue
              internalValueEraseJob?.cancel()
              onValueChange?.invoke(stepPolicy.stepValue(internalValue!!, valueRange))
            },
            onValueChangeFinished = { newValue ->
              onValueChangeFinished?.invoke(stepPolicy.stepValue(newValue, valueRange))
              internalValueEraseJob?.cancel()
              internalValueEraseJob = launch {
                delay(1.seconds)
                internalValue = null
              }
            },
            stepPolicy = stepPolicy,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
              thumbColor = MaterialTheme.colorScheme.tertiary,
              activeTrackColor = MaterialTheme.colorScheme.tertiary,
              inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(0.2f),
            )
          )
        }

        if (trailingContent != null)
          Box(
            modifier = Modifier.widthIn(min = 96.dp),
            contentAlignment = Alignment.TopEnd
          ) {
            ProvideContentColorTextStyle(
              contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
              textStyle = MaterialTheme.typography.headlineLarge
            ) {
              trailingContent(stepPolicy.stepValue(internalValue ?: value, valueRange))
            }
          }
      }
    },
    leading = leadingContent
  )
}

@Composable fun <T> SectionSlider(
  value: T,
  values: List<T>,
  modifier: Modifier = Modifier,
  onValueChange: ((T) -> Unit)? = null,
  onValueChangeFinished: ((T) -> Unit)? = null,
  headlineContent: @Composable () -> Unit,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: @Composable ((T) -> Unit)? = { Text(it.toString()) }
) {
  SectionSlider(
    value = values.indexOf(value),
    modifier = modifier,
    valueRange = 0..values.lastIndex,
    onValueChange = onValueChange?.let { { onValueChange(values[it]) } },
    onValueChangeFinished = onValueChangeFinished?.let { { onValueChangeFinished(values[it]) } },
    headlineContent = headlineContent,
    leadingContent = leadingContent,
    trailingContent = trailingContent?.let { { trailingContent(values[it]) } }
  )
}

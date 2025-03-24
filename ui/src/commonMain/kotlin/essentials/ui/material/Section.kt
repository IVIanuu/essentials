package essentials.ui.material

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.common.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Composable fun SectionContainer(
  modifier: Modifier = Modifier,
  sectionType: SectionType = inject,
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
      top = if (sectionType.first) if (sectionType.withHeader) 8.dp else 16.dp else 2.dp,
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

enum class SectionType(val first: Boolean, val last: Boolean, val withHeader: Boolean) {
  FIRST_WITH_HEADER(true, false, true),
  FIRST(true, false, false),
  LAST(false, true, false),
  MIDDLE(false, false, false),
  SINGLE(true, true, false),
  SINGLE_WITH_HEADER(true, true, true);
  @Provide companion object {
    @Provide val default = MIDDLE
  }
}

fun sectionTypeOf(index: Int, itemCount: Int, withHeader: Boolean) = when {
  index == 0 ->
    if (itemCount == 1)
        (if (withHeader) SectionType.SINGLE_WITH_HEADER else SectionType.SINGLE)
    else if (withHeader) SectionType.FIRST_WITH_HEADER else SectionType.FIRST
  index == itemCount - 1 -> SectionType.LAST
  else -> SectionType.MIDDLE
}

@Composable fun SectionAlert(
  modifier: Modifier = Modifier,
  sectionType: SectionType = inject,
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
  description: @Composable ColumnScope.(PaddingValues) -> Unit
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
      if (title != null || icon != null)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
          if (title != null)
            Box(modifier = Modifier.weight(1f)) {
              ProvideTextStyle(MaterialTheme.typography.bodyLarge) { title() }
            }

          if (icon != null)
            CompositionLocalProvider(LocalContentColor provides colors.iconColor) {
              icon()
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
  sectionType: SectionType = inject,
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
    padding = padding.copy(
      contentPadding = PaddingValues(
        start = padding.contentPadding.calculateStartPadding(LocalLayoutDirection.current),
        end = padding.contentPadding.calculateEndPadding(LocalLayoutDirection.current)
      )
    ),
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
          .padding(
            top = padding.contentPadding.calculateTopPadding(),
            bottom = padding.contentPadding.calculateBottomPadding()
          ),
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
  sectionType: SectionType = inject,
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
        interactionSource = interactionSource,
        colors = SwitchDefaults.colors(
          uncheckedTrackColor = Color.Transparent,
          checkedTrackColor = MaterialTheme.colorScheme.tertiary,
          checkedThumbColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp)
        )
      )
    }
  )
}

@Composable fun <T : Comparable<T>> SectionSlider(
  value: T,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  sectionType: SectionType = inject,
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

interface SectionScope {
  fun item(
    key: Any? = null,
    content: @Composable LazyItemScope.(@Provide SectionType) -> Unit
  )
}

inline fun <T> SectionScope.sectionItems(
  items: List<T>,
  crossinline key: (T) -> Any? = { null },
  crossinline itemContent: @Composable LazyItemScope.(T, @Provide SectionType) -> Unit
) {
  items.fastForEach { item ->
    item(key(item)) {
      itemContent(this, item, it)
    }
  }
}

inline fun <T> SectionScope.sectionItemsIndexed(
  items: List<T>,
  crossinline key: (Int, T) -> Any? = { _, _ -> null },
  crossinline itemContent: @Composable LazyItemScope.(Int, T, @Provide SectionType) -> Unit
) {
  items.fastForEachIndexed { index, item ->
    item(key(index, item)) {
      itemContent(this, index, item, it)
    }
  }
}

fun LazyListScope.section(
  header: (@Composable LazyItemScope.() -> Unit)? = null,
  headerKey: Any? = null,
  block: SectionScope.() -> Unit
) {
  class Item(
    val key: Any?,
    val content: @Composable LazyItemScope.(SectionType) -> Unit
  )

  val items = mutableListOf<Item>()
  block(
    object : SectionScope {
      override fun item(key: Any?, content: @Composable (LazyItemScope.(SectionType) -> Unit)) {
        items += Item(key, content)
      }
    }
  )

  if (header != null)
    item(headerKey, content = header)

  items.fastForEachIndexed { index, item ->
    item(item.key) {
      item.content(this, sectionTypeOf(index, items.size, header != null))
    }
  }
}

fun LazyListScope.singleItemSection(
  key: Any? = null,
  block: @Composable LazyItemScope.(@Provide SectionType) -> Unit
) {
  item(key) { block(SectionType.SINGLE) }
}

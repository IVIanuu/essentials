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
import essentials.ui.common.*

object SectionDefaults {
  @Composable fun containerColor(selected: Boolean, focused: Boolean, tone: Tone) =
    essentials.ui.material.containerColor(selected, focused, tone)

  @Composable fun contentColor(selected: Boolean, tone: Tone) = sectionTextColorForTone(selected, tone)

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

  fun padding(sectionType: SectionType) = PaddingValues(
    start = 16.dp,
    end = 16.dp,
    top = if (sectionType.first) 16.dp else 2.dp,
    bottom = if (sectionType.last) 16.dp else 2.dp
  )

  val contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)

  @Composable fun extraContentPadding(selected: Boolean) = PaddingValues(
    vertical = animateDpAsState(if (selected) 8.dp else 0.dp).value
  )
}

@Composable fun SectionContainer(
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  selected: Boolean = false,
  focused: Boolean = false,
  tone: Tone = Tone.NEUTRAL,
  containerColor: Color = SectionDefaults.containerColor(selected, focused, tone),
  contentColor: Color = SectionDefaults.contentColor(selected, tone),
  shape: Shape = SectionDefaults.shape(sectionType),
  padding: PaddingValues = SectionDefaults.padding(sectionType),
  contentPadding: PaddingValues = SectionDefaults.contentPadding,
  extraContentPadding: PaddingValues = SectionDefaults.extraContentPadding(selected),
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable () -> Unit
) {
  val currentContainerColor by animateColorAsState(containerColor)
  val currentContentColor by animateColorAsState(contentColor)
  Surface(
    color = currentContainerColor,
    contentColor = currentContentColor,
    modifier = modifier.padding(padding),
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
        .padding(contentPadding)
        .padding(extraContentPadding)
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
  tone: Tone = Tone.NEUTRAL,
  containerColor: Color = SectionDefaults.containerColor(selected, focused, tone),
  contentColor: Color = SectionDefaults.contentColor(selected, tone),
  shape: Shape = SectionDefaults.shape(sectionType),
  padding: PaddingValues = SectionDefaults.padding(sectionType),
  contentPadding: PaddingValues = SectionDefaults.contentPadding,
  extraContentPadding: PaddingValues = SectionDefaults.extraContentPadding(selected),
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  title: (@Composable RowScope.() -> Unit)? = null,
  icon: (@Composable RowScope.() -> Unit)? = null,
  actions: (@Composable RowScope.() -> Unit)? = null,
  text: @Composable ColumnScope.(PaddingValues) -> Unit
) {
  SectionContainer(
    modifier = modifier,
    sectionType = sectionType,
    selected = selected,
    focused = focused,
    tone = tone,
    containerColor = containerColor,
    contentColor = contentColor,
    shape = shape,
    padding = padding,
    contentPadding = contentPadding,
    extraContentPadding = extraContentPadding,
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
            icon()
        }
      }

      ProvideContentColorTextStyle(
        LocalContentColor.current.copy(alpha = ContentAlpha.Medium),
        MaterialTheme.typography.bodyMedium
      ) {
        text(PaddingValues(
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
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  sectionType: SectionType = SectionType.MIDDLE,
  selected: Boolean = false,
  onClick: (() -> Unit)? = null,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
  SectionContainer(
    modifier = modifier,
    sectionType = sectionType,
    selected = selected,
    onClick = onClick,
    interactionSource = interactionSource
  ) {
    EsListItem(
      contentPadding = PaddingValues(0.dp),
      headlineContent = headlineContent,
      supportingContent = supportingContent,
      leadingContent = leadingContent?.let {
        {
          CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.secondary,
            content = leadingContent
          )
        }
      },
      trailingContent = trailingContent?.let {
        {
          CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.secondary,
            content = trailingContent
          )
        }
      },
      interactionSource = interactionSource
    )
  }
}

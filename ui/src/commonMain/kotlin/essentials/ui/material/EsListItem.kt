package essentials.ui.material

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import essentials.ui.common.*

@Composable fun DecoratedListItem(
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  first: Boolean = false,
  last: Boolean = false,
  selected: Boolean = false,
  onClick: (() -> Unit)? = null,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
  val color by animateColorAsState(
    if (selected) MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp)
  )
  val contentColor by animateColorAsState(
    if (selected) MaterialTheme.colorScheme.onTertiary
    else MaterialTheme.colorScheme.onSurface
  )
  Surface(
    color = color,
    contentColor = contentColor,
    modifier = modifier.padding(
      start = 16.dp,
      end = 16.dp,
      top = if (first) 16.dp else 2.dp,
      bottom = if (last) 16.dp else 2.dp
    ),
    shape = firstLastCornersVertical(first, last)
  ) {
    val innerPadding by animateDpAsState(
      if (selected) 8.dp else 0.dp
    )

    EsListItem(
      modifier = Modifier.padding(vertical = innerPadding),
      onClick = onClick,
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
      trailingContent = trailingContent,
      interactionSource = interactionSource
    )
  }
}

@Composable fun EsListItem(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  headlineContent: (@Composable () -> Unit)? = null,
  supportingContent: (@Composable () -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
  textPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
  contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
  Row(
    modifier = (onClick?.let {
      Modifier.clickable(
        onClick = it,
        interactionSource = interactionSource,
        indication = ripple()
      )
    } ?: Modifier)
      .padding(contentPadding)
      .then(modifier),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // leading
    if (leadingContent != null) {
      Box(contentAlignment = Alignment.Center) {
        ProvideContentColorTextStyle(
          contentColor = LocalContentColor.current.copy(alpha = ContentAlpha.Medium),
          textStyle = MaterialTheme.typography.labelMedium,
          content = leadingContent
        )
      }
    }

    // text
    Column(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically)
        .padding(
          start = if (leadingContent != null) textPadding.calculateStartPadding(LocalLayoutDirection.current)
          else 0.dp,
          top = 0.dp,
          end = if (trailingContent != null) textPadding.calculateEndPadding(LocalLayoutDirection.current)
          else 0.dp,
          bottom = 0.dp
        ),
      verticalArrangement = Arrangement.Center
    ) {
      if (headlineContent != null)
        ProvideContentColorTextStyle(
          contentColor = LocalContentColor.current,
          textStyle = MaterialTheme.typography.bodyLarge,
          content = headlineContent
        )
      if (supportingContent != null) {
        Spacer(Modifier.height(4.dp))

        ProvideContentColorTextStyle(
          contentColor = LocalContentColor.current.copy(alpha = ContentAlpha.Medium),
          textStyle = MaterialTheme.typography.bodyMedium,
          content = supportingContent
        )
      }
    }

    // trailing
    if (trailingContent != null)
      Box(contentAlignment = Alignment.Center) {
        ProvideContentColorTextStyle(
          contentColor = LocalContentColor.current.copy(alpha = ContentAlpha.Medium),
          textStyle = MaterialTheme.typography.labelMedium,
          content = trailingContent
        )
    }
  }
}

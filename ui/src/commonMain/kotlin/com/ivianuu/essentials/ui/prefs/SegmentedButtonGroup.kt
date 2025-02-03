package com.ivianuu.essentials.ui.prefs

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.injekt.*

@Composable fun <T> SingleChoiceToggleButtonGroupListItem(
  values: Collection<T>,
  selected: T,
  onSelectionChanged: (T) -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  renderer: UiRenderer<T> = inject,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  BaseToggleButtonGroupListItem(
    values = values,
    isSelected = { it == selected },
    onSelectionChangeRequest = {
      if (it != selected)
        onSelectionChanged(it)
    },
    modifier = modifier,
    leadingContent = leadingContent,
    headlineContent = headlineContent,
    trailingContent = trailingContent
  )
}

@Composable fun <T> OptionalChoiceToggleButtonGroupListItem(
  values: Collection<T>,
  selected: T?,
  onSelectionChanged: (T?) -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  renderer: UiRenderer<T> = inject,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  BaseToggleButtonGroupListItem(
    values = values,
    isSelected = { it == selected },
    onSelectionChangeRequest = {
      onSelectionChanged(if (it != selected) it else null)
    },
    modifier = modifier,
    leadingContent = leadingContent,
    headlineContent = headlineContent,
    trailingContent = trailingContent
  )
}

@Composable fun <T> MultiChoiceToggleButtonGroupListItem(
  values: Collection<T>,
  selected: Set<T>,
  onSelectionChanged: (Set<T>) -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  renderer: UiRenderer<T> = inject,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  BaseToggleButtonGroupListItem(
    values = values,
    isSelected = { it in selected },
    onSelectionChangeRequest = {
      onSelectionChanged(
        if (it in selected) selected - it
        else selected + it
      )
    },
    modifier = modifier,
    leadingContent = leadingContent,
    headlineContent = headlineContent,
    trailingContent = trailingContent
  )
}

@Composable fun <T> BaseToggleButtonGroupListItem(
  values: Collection<T>,
  isSelected: (T) -> Boolean,
  onSelectionChangeRequest: (T) -> Unit,
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  renderer: UiRenderer<T> = inject,
  leadingContent: (@Composable () -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  EsListItem(
    modifier = modifier,
    leadingContent = leadingContent,
    headlineContent = headlineContent,
    supportingContent = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        values.forEach { value ->
          val targetBackgroundColor = if (isSelected(value)) MaterialTheme.colorScheme.secondary
          else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
          val backgroundColor by animateColorAsState(targetBackgroundColor)
          val contentColor by animateColorAsState(guessingContentColorFor(targetBackgroundColor))
          Button(
            modifier = Modifier.height(32.dp),
            onClick = { onSelectionChangeRequest(value) },
            contentPadding = PaddingValues(horizontal = 8.dp)
          ) {
            Text(
              text = renderer.render(value),
              maxLines = 1
            )
          }
        }
      }
    },
    trailingContent = trailingContent
  )
}

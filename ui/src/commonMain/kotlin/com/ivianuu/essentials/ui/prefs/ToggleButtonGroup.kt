package com.ivianuu.essentials.ui.prefs

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
  modifier: Modifier = Modifier,
  @Inject renderer: UiRenderer<T>,
  leading: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
) {
  BaseToggleButtonGroupListItem(
    values = values,
    isSelected = { it == selected },
    onSelectionChangeRequest = {
      if (it != selected)
        onSelectionChanged(it)
    },
    modifier = modifier,
    leading = leading,
    title = title,
    subtitle = subtitle,
    trailing = trailing
  )
}

@Composable fun <T> OptionalChoiceToggleButtonGroupListItem(
  values: Collection<T>,
  selected: T?,
  onSelectionChanged: (T?) -> Unit,
  modifier: Modifier = Modifier,
  @Inject renderer: UiRenderer<T>,
  leading: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
) {
  BaseToggleButtonGroupListItem(
    values = values,
    isSelected = { it == selected },
    onSelectionChangeRequest = {
      onSelectionChanged(if (it != selected) it else null)
    },
    modifier = modifier,
    leading = leading,
    title = title,
    subtitle = subtitle,
    trailing = trailing
  )
}

@Composable fun <T> MultiChoiceToggleButtonGroupListItem(
  values: Collection<T>,
  selected: Set<T>,
  onSelectionChanged: (Set<T>) -> Unit,
  modifier: Modifier = Modifier,
  @Inject renderer: UiRenderer<T>,
  leading: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
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
    leading = leading,
    title = title,
    subtitle = subtitle,
    trailing = trailing
  )
}

@Composable fun <T> BaseToggleButtonGroupListItem(
  values: Collection<T>,
  isSelected: (T) -> Boolean,
  onSelectionChangeRequest: (T) -> Unit,
  modifier: Modifier = Modifier,
  @Inject renderer: UiRenderer<T>,
  leading: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
) {
  ListItem(
    modifier = modifier,
    leading = leading,
    title = title,
    subtitle = {
      subtitle?.invoke()

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        values.forEach { value ->
          val targetBackgroundColor = if (isSelected(value)) MaterialTheme.colors.secondary
          else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
          val backgroundColor by animateColorAsState(targetBackgroundColor)
          val contentColor by animateColorAsState(guessingContentColorFor(targetBackgroundColor))
          Button(
            modifier = Modifier.height(32.dp),
            colors = ButtonDefaults.esButtonColors(
              backgroundColor = backgroundColor,
              contentColor = contentColor
            ),
            onClick = { onSelectionChangeRequest(value) },
            forceMinSize = false,
            contentPadding = PaddingValues(horizontal = 8.dp)
          ) {
            Text(
              text = renderer(value),
              maxLines = 1
            )
          }
        }
      }
    },
    trailing = trailing
  )
}

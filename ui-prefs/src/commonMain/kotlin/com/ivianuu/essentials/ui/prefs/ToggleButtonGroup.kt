package com.ivianuu.essentials.ui.prefs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.esButtonColors
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.injekt.Inject

@Composable fun <T> SingleChoiceToggleButtonGroupListItem(
  values: Collection<T>,
  selected: T,
  onSelectionChanged: (T) -> Unit,
  modifier: Modifier = Modifier,
  @Inject renderer: UiRenderer<T>,
  leading: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
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
    subtitle = subtitle
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
    subtitle = subtitle
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
) {
  val minHeight = if (subtitle != null) 96.dp else 88.dp

  ListItem(
    modifier = modifier
      .heightIn(minHeight),
    leading = leading,
    title = title,
    subtitle = {
      subtitle?.invoke()

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        values.forEach { value ->
          val targetBackgroundColor = if (isSelected(value)) MaterialTheme.colors.secondary
          else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
          val backgroundColor by animateColorAsState(targetBackgroundColor)
          val contentColor by animateColorAsState(guessingContentColorFor(targetBackgroundColor))
          Button(
            colors = ButtonDefaults.esButtonColors(
              backgroundColor = backgroundColor,
              contentColor = contentColor
            ),
            onClick = { onSelectionChangeRequest(value) }
          ) {
            Text(renderer(value))
          }
        }
      }
    }
  )
}

/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.ivianuu.essentials.ui.material.HorizontalDivider

@Composable fun Dialog(
  modifier: Modifier = Modifier,
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  buttons: @Composable (() -> Unit)? = null,
  content: @Composable (() -> Unit)? = null,
  showTopDivider: Boolean = false,
  showBottomDivider: Boolean = false,
  applyContentPadding: Boolean = true,
) {
  BaseDialog(modifier = modifier) {
    DialogBody(
      showTopDivider = showTopDivider,
      showBottomDivider = showBottomDivider,
      applyContentPadding = applyContentPadding,
      icon = icon,
      title = title,
      content = content,
      buttons = buttons
    )
  }
}

@Composable private fun DialogBody(
  icon: @Composable (() -> Unit)?,
  title: @Composable (() -> Unit)?,
  content: @Composable (() -> Unit)?,
  buttons: @Composable() (() -> Unit)?,
  showTopDivider: Boolean = false,
  showBottomDivider: Boolean = false,
  applyContentPadding: Boolean
) {
  val header: @Composable (() -> Unit)? = if (icon != null || title != null) {
    {
      val styledTitle: @Composable (() -> Unit)? = title?.let {
        {
          ProvideTextStyle(MaterialTheme.typography.h6) {
            CompositionLocalProvider(
              LocalContentAlpha provides ContentAlpha.high,
              content = title
            )
          }
        }
      }

      val styledIcon: @Composable (() -> Unit)? = icon?.let {
        {
          CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.high,
            content = icon
          )
        }
      }

      if (styledIcon != null && styledTitle != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          styledIcon()
          Spacer(Modifier.width(16.dp))
          styledTitle()
        }
      } else if (styledIcon != null) {
        styledIcon()
      } else if (styledTitle != null) {
        styledTitle()
      }
    }
  } else {
    null
  }

  val finalContent: @Composable (() -> Unit)? = if (content != null) {
    {
      ProvideTextStyle(MaterialTheme.typography.subtitle1) {
        CompositionLocalProvider(
          LocalContentAlpha provides ContentAlpha.medium,
          content = content
        )
      }
    }
  } else {
    null
  }

  DialogContentLayout(
    showTopDivider = showTopDivider,
    showBottomDivider = showBottomDivider,
    applyContentPadding = applyContentPadding,
    header = header,
    content = finalContent,
    buttons = buttons
  )
}

@Composable private fun DialogContentLayout(
  showTopDivider: Boolean = false,
  showBottomDivider: Boolean = false,
  applyContentPadding: Boolean,
  header: @Composable (() -> Unit)?,
  content: @Composable (() -> Unit)?,
  buttons: @Composable (() -> Unit)?
) {
  val finalContent: @Composable () -> Unit = {
    if (header != null) {
      Box(
        modifier = Modifier.padding(
          start = 24.dp,
          top = 24.dp,
          end = 24.dp,
          bottom = if (buttons != null && content == null) 28.dp else 24.dp
        ).layoutId(DialogContentSlot.Header)
      ) { header() }
    }

    if (content != null) {
      if (header != null && showTopDivider) {
        HorizontalDivider(modifier = Modifier.layoutId(DialogContentSlot.TopDivider))
      }

      Box(
        modifier = Modifier.padding(
          start = if (applyContentPadding) 24.dp else 0.dp,
          top = if (header == null) 24.dp else 0.dp,
          end = if (applyContentPadding) 24.dp else 0.dp,
          bottom = if (buttons == null) 24.dp else 0.dp
        ).layoutId(DialogContentSlot.Content)
      ) { content() }
    }

    if (buttons != null) {
      if (content != null && showBottomDivider) {
        HorizontalDivider(modifier = Modifier.layoutId(DialogContentSlot.BottomDivider))
      }

      FlowRow(
        modifier = Modifier
          .layoutId(DialogContentSlot.Buttons)
          .padding(
            start = 8.dp,
            top = if (!showBottomDivider && content != null) 36.dp else 8.dp,
            end = 8.dp,
            bottom = 8.dp
          ),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 12.dp,
        mainAxisAlignment = FlowMainAxisAlignment.End,
        content = buttons
      )
    }
  }

  Layout(content = finalContent) { measurables, constraints ->
    var childConstraints = constraints.copy(
      minWidth = constraints.maxWidth,
      minHeight = 0
    )

    val headerMeasureable =
      measurables.firstOrNull { it.layoutId == DialogContentSlot.Header }
    val topDividerMeasureable =
      measurables.firstOrNull { it.layoutId == DialogContentSlot.TopDivider }
    val contentMeasureable =
      measurables.firstOrNull { it.layoutId == DialogContentSlot.Content }
    val bottomDividerMeasureable =
      measurables.firstOrNull { it.layoutId == DialogContentSlot.BottomDivider }
    val buttonsMeasureable =
      measurables.firstOrNull { it.layoutId == DialogContentSlot.Buttons }

    fun measureFixed(measureable: Measurable?): Placeable? {
      return if (measureable != null) {
        val placeable = measureable.measure(childConstraints)
        childConstraints = childConstraints.copy(
          maxHeight = childConstraints.maxHeight - placeable.height
        )
        placeable
      } else {
        null
      }
    }

    val headerPlaceable = measureFixed(headerMeasureable)
    val topDividerPlaceable = measureFixed(topDividerMeasureable)
    val bottomDividerPlaceable = measureFixed(bottomDividerMeasureable)
    val buttonsPlaceable = measureFixed(buttonsMeasureable)
    val contentPlaceable = measureFixed(contentMeasureable)

    val placeables = listOfNotNull(
      headerPlaceable,
      topDividerPlaceable,
      contentPlaceable,
      bottomDividerPlaceable,
      buttonsPlaceable
    )
    val height = placeables.map { it.height }.sum()

    layout(width = constraints.maxWidth, height = height) {
      var offsetY = 0
      placeables.forEach { placeable ->
        placeable.place(0, offsetY)
        offsetY += placeable.height
      }
    }
  }
}

private enum class DialogContentSlot {
  Header, TopDivider, Content, BottomDivider, Buttons
}

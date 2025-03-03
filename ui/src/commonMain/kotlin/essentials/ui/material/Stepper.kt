/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import essentials.ui.common.ProvideContentColorTextStyle

@Composable fun Step(
  step: Int,
  isCurrent: Boolean,
  isCompleted: Boolean,
  onClick: () -> Unit,
  title: @Composable () -> Unit,
  content: (@Composable () -> Unit)? = null,
  actions: @Composable () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth()
      .padding(start = 8.dp, top = 8.dp, end = 8.dp)
      .border(
        1.dp,
        LocalContentColor.current.copy(alpha = 0.12f),
        RoundedCornerShape(8.dp)
      )
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.Center
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val targetContainerColor = if (isCurrent || isCompleted) MaterialTheme.colorScheme.secondary
      else LocalContentColor.current.copy(alpha = 0.38f)
      val containerColor = animateColorAsState(targetContainerColor).value
      val contentColor = animateColorAsState(guessingContentColorFor(targetContainerColor)).value
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(containerColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
          if (isCompleted) {
            Icon(
              modifier = Modifier.padding(4.dp),
              imageVector = Icons.Default.Done,
              contentDescription = null
            )
          } else {
            Text(
              text = step.toString(),
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }

      Spacer(Modifier.width(16.dp))

      ProvideContentColorTextStyle(
        contentColor = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.titleLarge,
        content = title
      )
    }

    AnimatedVisibility(
      modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
      visible = isCurrent,
      enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
      exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
    ) {
      Column {
        if (content != null) {
          ProvideContentColorTextStyle(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textStyle = MaterialTheme.typography.bodyMedium,
            content = content
          )

          Spacer(Modifier.padding(bottom = 16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          actions()
        }
      }
    }
  }
}

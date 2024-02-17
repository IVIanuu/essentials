/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.stepper

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.material.*

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
      val targetBackgroundColor = if (isCurrent || isCompleted) MaterialTheme.colors.secondary
      else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
      val backgroundColor = animateColorAsState(targetBackgroundColor).value
      val contentColor = animateColorAsState(guessingContentColorFor(targetBackgroundColor)).value
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(backgroundColor, CircleShape),
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
              style = MaterialTheme.typography.caption
            )
          }
        }
      }

      Spacer(Modifier.width(16.dp))

      CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.subtitle1,
        LocalContentAlpha provides ContentAlpha.high,
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
          CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.body2,
            LocalContentAlpha provides ContentAlpha.medium,
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

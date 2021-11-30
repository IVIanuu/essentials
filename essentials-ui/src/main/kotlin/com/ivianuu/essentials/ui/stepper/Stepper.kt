/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.stepper

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.R
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
      .clickable(onClick = onClick)
      .padding(horizontal = 24.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth()
        .height(48.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val backgroundColor = if (isCurrent || isCompleted) MaterialTheme.colors.secondary
      else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CompositionLocalProvider(
          LocalContentColor provides guessingContentColorFor(backgroundColor)
        ) {
          if (isCompleted) {
            Icon(
              modifier = Modifier.padding(4.dp),
              painterResId = R.drawable.es_ic_done
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

    Spacer(Modifier.height(8.dp))

    Row {
      VerticalDivider(
        modifier = Modifier
          .padding(start = 24.dp)
      )

      Spacer(Modifier.width(40.dp))

      if (isCurrent) {
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
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun Dialog(
  modifier: Modifier = Modifier,
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  buttons: (@Composable () -> Unit)? = null,
  content: (@Composable () -> Unit)? = null,
  applyContentPadding: Boolean = true,
) {
  DialogContainer(modifier = modifier) {
    Column {
      val hasHeader = icon != null || title != null

      @Composable fun StyledTitle() {
        CompositionLocalProvider(
          LocalTextStyle provides MaterialTheme.typography.h6,
          LocalContentAlpha provides ContentAlpha.high,
          content = title!!
        )
      }

      @Composable fun StyledIcon() {
        CompositionLocalProvider(
          LocalContentAlpha provides ContentAlpha.high,
          content = icon!!
        )
      }

      if (icon != null || title != null)
        Box(
          modifier = Modifier.padding(
            start = 24.dp,
            top = 24.dp,
            end = 24.dp,
            bottom = if (buttons != null && content == null) 28.dp else 24.dp
          )
        ) {
          if (icon != null && title != null)
            Row(verticalAlignment = Alignment.CenterVertically) {
              StyledIcon()
              Spacer(Modifier.width(16.dp))
              StyledTitle()
            }
          else if (icon != null)
            StyledIcon()
          else if (title != null)
            StyledTitle()
        }

      if (content != null) {
        Box(
          modifier = Modifier
            .weight(1f, false)
            .padding(
              start = if (applyContentPadding) 24.dp else 0.dp,
              top = if (!hasHeader) 24.dp else 0.dp,
              end = if (applyContentPadding) 24.dp else 0.dp,
              bottom = if (buttons == null) 24.dp else 0.dp
            )
        ) {
          CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.body2,
            LocalContentAlpha provides ContentAlpha.medium,
            content = content
          )
        }
      }

      if (buttons != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              start = 8.dp,
              top = 16.dp,
              end = 8.dp,
              bottom = 8.dp
            ),
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
          verticalAlignment = Alignment.CenterVertically,
          content = { buttons() }
        )
      }
    }
  }
}

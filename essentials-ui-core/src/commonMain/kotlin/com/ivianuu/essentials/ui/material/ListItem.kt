/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*

@Composable fun ListItem(
  modifier: Modifier = Modifier,
  title: @Composable (() -> Unit)? = null,
  subtitle: @Composable (() -> Unit)? = null,
  leading: @Composable (() -> Unit)? = null,
  trailing: @Composable (() -> Unit)? = null,
  contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
  textPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
  val minHeight = if (subtitle != null) {
    if (leading == null) 72.dp else 80.dp
  } else {
    if (leading == null) 56.dp else 64.dp
  }

  Box(
    modifier = modifier
      .defaultMinSize(minHeight = minHeight)
      .fillMaxWidth(),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // leading
      if (leading != null) {
        Box(
          modifier = Modifier
            .heightIn(minHeight),
          contentAlignment = Alignment.CenterStart
        ) {
          Box(
            modifier = Modifier
              .padding(
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding()
              ),
            contentAlignment = Alignment.Center
          ) {
            CompositionLocalProvider(
              LocalContentAlpha provides ContentAlpha.high,
              content = leading
            )
          }
        }
      }

      // content
      Box(
        modifier = Modifier
          .weight(1f)
          .padding(textPadding),
        contentAlignment = Alignment.CenterStart
      ) {
        Column(
          modifier = Modifier
            .padding(
              top = contentPadding.calculateTopPadding(),
              bottom = contentPadding.calculateBottomPadding()
            ),
          verticalArrangement = Arrangement.Center
        ) {
          if (title != null)
            CompositionLocalProvider(
              LocalTextStyle provides MaterialTheme.typography.subtitle1,
              LocalContentAlpha provides ContentAlpha.high,
              content = title
            )
          if (subtitle != null)
            CompositionLocalProvider(
              LocalTextStyle provides MaterialTheme.typography.body2,
              LocalContentAlpha provides ContentAlpha.medium,
              content = subtitle
            )
        }
      }

      // trailing
      if (trailing != null) {
        Box(
          modifier = Modifier
            .height(minHeight),
          contentAlignment = Alignment.CenterEnd
        ) {
          Box(
            modifier = Modifier.padding(
              top = contentPadding.calculateTopPadding(),
              end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
              bottom = contentPadding.calculateBottomPadding()
            ),
            contentAlignment = Alignment.Center
          ) {
            CompositionLocalProvider(
              LocalContentAlpha provides ContentAlpha.high,
              content = trailing
            )
          }
        }
      }
    }
  }
}

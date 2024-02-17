/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable fun ListItem(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
  leadingPadding: PaddingValues = PaddingValues(start = 16.dp),
  textPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
  trailingPadding: PaddingValues = PaddingValues(end = 16.dp)
) {
  val minHeight = if (subtitle != null) {
    if (leading == null) 64.dp else 72.dp
  } else {
    if (leading == null) 48.dp else 56.dp
  }

  Row(
    modifier = (onClick?.let { Modifier.clickable(onClick = it) } ?: Modifier)
      .then(modifier)
      .heightIn(minHeight),
    horizontalArrangement = Arrangement.Center
  ) {
    // leading
    if (leading != null) {
      Box(
        modifier = Modifier
          .height(minHeight)
          .padding(leadingPadding),
        contentAlignment = Alignment.Center
      ) {
        CompositionLocalProvider(
          LocalContentAlpha provides ContentAlpha.high,
          content = leading
        )
      }
    }

    // text
    Column(
      modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically)
        .padding(textPadding),
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

    // trailing
    if (trailing != null) {
      Box(
        modifier = Modifier
          .height(minHeight)
          .padding(trailingPadding),
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

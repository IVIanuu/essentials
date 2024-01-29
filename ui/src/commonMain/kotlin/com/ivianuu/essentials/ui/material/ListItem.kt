/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun ListItem(
  modifier: Modifier = Modifier,
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
    modifier = modifier.heightIn(minHeight),
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

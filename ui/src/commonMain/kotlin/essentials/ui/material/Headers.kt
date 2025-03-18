/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import essentials.ui.common.*

@Composable fun Subheader(
  modifier: Modifier = Modifier,
  trailing: (@Composable () -> Unit)? = null,
  title: @Composable () -> Unit
) {
  Row(
    modifier = modifier
      .height(48.dp)
      .padding(horizontal = 32.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompositionLocalProvider(
      LocalTextStyle provides MaterialTheme.typography.titleLarge,
      content = title
    )

    Spacer(Modifier.weight(1f))

    if (trailing != null)
      CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.tertiary,
        content = trailing
      )
  }
}

@Composable fun SectionHeader(
  modifier: Modifier = Modifier,
  text: @Composable () -> Unit
) {
  Box(
    modifier = modifier.padding(start = 40.dp, top = 8.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    ProvideContentColorTextStyle(
      textStyle = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.Medium
      ),
      contentColor = MaterialTheme.colorScheme.primary,
      content = text
    )
  }
}

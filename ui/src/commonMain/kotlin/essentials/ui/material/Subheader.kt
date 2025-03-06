/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable fun Subheader(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit
) {
  Row(
    modifier = modifier
      .height(48.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Spacer(Modifier.width(16.dp))

    CompositionLocalProvider(
      LocalTextStyle provides MaterialTheme.typography.titleLarge,
      content = title
    )

    Spacer(Modifier.width(16.dp))
  }
}

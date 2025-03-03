/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.ui.common.ProvideContentColorTextStyle

@Composable fun Subheader(modifier: Modifier = Modifier, text: @Composable () -> Unit) {
  Box(
    modifier = Modifier
      .height(48.dp)
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .then(modifier),
    contentAlignment = Alignment.CenterStart
  ) {
    ProvideContentColorTextStyle(
      contentColor = MaterialTheme.colorScheme.secondary,
      textStyle = MaterialTheme.typography.bodyMedium,
      content = text
    )
  }
}

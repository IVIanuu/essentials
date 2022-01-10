/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable fun Popup(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.padding(8.dp),
    elevation = 8.dp,
    shape = MaterialTheme.shapes.medium
  ) {
    Box(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
      content()
    }
  }
}

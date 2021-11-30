/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

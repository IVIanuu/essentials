/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun Popup(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.widthIn(112.dp, 280.dp),
    shadowElevation = 3.dp,
    shape = MaterialTheme.shapes.extraSmall
  ) {
    Column(
      modifier = Modifier
        .padding(12.dp)
        .verticalScroll(rememberScrollState())
        .padding(top = 8.dp, bottom = 8.dp)
    ) {
      content()
    }
  }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun PopupContainer(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier
      .padding(8.dp)
      .animateContentSize(),
    elevation = 8.dp,
    shape = MaterialTheme.shapes.medium
  ) {
    Column(
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(top = 8.dp, bottom = 8.dp)
    ) {
      content()
    }
  }
}

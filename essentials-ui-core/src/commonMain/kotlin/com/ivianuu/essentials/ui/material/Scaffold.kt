/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.layout.navigationBars
import com.ivianuu.essentials.ui.layout.navigationBarsPadding
import com.ivianuu.essentials.ui.layout.statusBars

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Scaffold(
  modifier: Modifier = Modifier,
  topBar: (@Composable () -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = contentColorFor(containerColor),
  applyInsets: Boolean = true,
  content: @Composable () -> Unit
) {
  androidx.compose.material3.Scaffold(
    modifier = modifier,
    topBar = topBar ?: {},
    bottomBar = bottomBar ?: {},
    floatingActionButton = if (floatingActionButton != null) (
        {
          Box(
            modifier = if (applyInsets) Modifier.navigationBarsPadding() else Modifier
          ) {
            floatingActionButton()
          }
        }
        ) else ({}),
    containerColor = containerColor,
    contentColor = contentColor
  ) {
    Box(
      modifier = Modifier
        .then(
          if (applyInsets && topBar != null) Modifier.consumeWindowInsets(WindowInsets.statusBars)
          else Modifier
        )
        .then(
          if (applyInsets && bottomBar != null) Modifier.consumeWindowInsets(WindowInsets.navigationBars)
          else Modifier
        )
    ) {
      content()
    }
  }
}

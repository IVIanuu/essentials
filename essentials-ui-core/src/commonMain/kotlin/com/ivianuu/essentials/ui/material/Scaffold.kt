/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.layout.navigationBars
import com.ivianuu.essentials.ui.layout.navigationBarsPadding
import com.ivianuu.essentials.ui.layout.statusBars

@Composable fun Scaffold(
  modifier: Modifier = Modifier,
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  topBar: (@Composable () -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  backgroundColor: Color = MaterialTheme.colors.background,
  contentColor: Color = contentColorFor(backgroundColor),
  applyInsets: Boolean = true,
  content: @Composable () -> Unit
) {
  androidx.compose.material.Scaffold(
    modifier = modifier,
    scaffoldState = scaffoldState,
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
    floatingActionButtonPosition = floatingActionButtonPosition,
    backgroundColor = backgroundColor,
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

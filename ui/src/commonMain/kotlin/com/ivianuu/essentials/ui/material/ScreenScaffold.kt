/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*

@Composable fun ScreenScaffold(
  modifier: Modifier = Modifier,
  topBar: (@Composable () -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  backgroundColor: Color = MaterialTheme.colors.background,
  scrollTopBar: Boolean = topBar != null,
  maxTopBarSize: Dp = 56.dp + with(LocalDensity.current) {
    WindowInsets.systemBars.getTop(LocalDensity.current).toDp()
  },
  content: @Composable () -> Unit
) {
  val topBarScrollState = if (!scrollTopBar) null
  else rememberTopBarScrollState()
    .also {
      val maxHeightOffset =
        with(LocalDensity.current) { -maxTopBarSize.toPx() }
      if (it.maxHeightOffset != maxHeightOffset) {
        it.maxHeightOffset = maxHeightOffset
      }
    }

  Scaffold(
    modifier = modifier
      .windowInsetsPadding(
        WindowInsets(
          left = WindowInsets.safeContent.getLeft(LocalDensity.current, LocalLayoutDirection.current),
          right = WindowInsets.safeContent.getRight(LocalDensity.current, LocalLayoutDirection.current)
        )
      )
      .then(
        if (topBarScrollState == null) Modifier
        else Modifier.nestedScroll(topBarScrollState)
      ),
    topBar = topBar?.let {
      if (topBarScrollState == null) topBar
      else ({
        Box(
          modifier = Modifier.heightIn(
            max = with(LocalDensity.current) {
              maxTopBarSize + topBarScrollState.heightOffset.toDp()
            }
          ),
          propagateMinConstraints = true
        ) {
          topBar()
        }
      })
    } ?: {},
    bottomBar = bottomBar ?: {},
    floatingActionButton = floatingActionButton?.let {
      {
        Box(modifier = Modifier.safeContentPadding()) {
          floatingActionButton()
        }
      }
    } ?: {},
    floatingActionButtonPosition = floatingActionButtonPosition,
    backgroundColor = backgroundColor
  ) {
    Box(
      modifier = Modifier.consumeWindowInsets(
        PaddingValues(
          top = if (topBar != null) Dp.Infinity else 0.dp,
          bottom = if (bottomBar != null) Dp.Infinity else 0.dp
        )
      ),
      propagateMinConstraints = true
    ) {
      content()
    }
  }
}

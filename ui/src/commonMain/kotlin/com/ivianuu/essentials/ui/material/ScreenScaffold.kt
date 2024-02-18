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
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.ui.insets.*

@Composable fun ScreenScaffold(
  modifier: Modifier = Modifier,
  state: ScaffoldState = rememberScaffoldState(),
  topBar: (@Composable () -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  isFloatingActionButtonDocked: Boolean = false,
  backgroundColor: Color = MaterialTheme.colors.background,
  applyInsets: Boolean = true,
  scrollTopBar: Boolean = topBar != null,
  maxTopBarSize: Dp = 56.dp + LocalInsets.current.top,
  content: @Composable () -> Unit
) {
  InsetsPadding(left = applyInsets, top = false, right = applyInsets, bottom = false) {
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
        .then(
          if (topBarScrollState == null) Modifier
          else Modifier.nestedScroll(topBarScrollState)
        ),
      scaffoldState = state,
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
      floatingActionButton = if (floatingActionButton != null) (
          {
            InsetsPadding(
              top = applyInsets && topBar == null,
              bottom = applyInsets && bottomBar == null
            ) {
              Box {
                floatingActionButton()
              }
            }
          }
          ) else ({}),
      floatingActionButtonPosition = floatingActionButtonPosition,
      isFloatingActionButtonDocked = isFloatingActionButtonDocked,
      backgroundColor = backgroundColor
    ) { bodyPadding ->
      val insets = if (applyInsets) LocalInsets.current else Insets()
      CompositionLocalProvider(
        LocalInsets provides Insets(
          left = max(bodyPadding.calculateLeftPadding(LocalLayoutDirection.current), insets.left),
          top = if (topBar == null) insets.top else bodyPadding.calculateTopPadding(),
          right = max(
            bodyPadding.calculateRightPadding(LocalLayoutDirection.current),
            insets.right
          ),
          bottom = if (bottomBar == null) insets.bottom else bodyPadding.calculateBottomPadding()
        ),
        content = content
      )
    }
  }
}

/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.insets.*

@Composable fun Scaffold(
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  topBar: @Composable (() -> Unit)? = null,
  bottomBar: @Composable (() -> Unit)? = null,
  floatingActionButton: @Composable (() -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  isFloatingActionButtonDocked: Boolean = false,
  drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
  drawerShape: Shape = MaterialTheme.shapes.large,
  drawerElevation: Dp = DrawerDefaults.Elevation,
  backgroundColor: Color = MaterialTheme.colors.background,
  applyInsets: Boolean = true,
  bodyContent: @Composable () -> Unit
) {
  InsetsPadding(
    left = applyInsets,
    top = false,
    right = applyInsets,
    bottom = false
  ) {
    Scaffold(
      scaffoldState = scaffoldState,
      topBar = topBar ?: {},
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
      drawerContent = drawerContent,
      drawerShape = drawerShape,
      drawerElevation = drawerElevation,
      backgroundColor = backgroundColor
    ) { bodyPadding ->
      val insets = if (applyInsets) LocalInsets.current else Insets()
      InsetsProvider(
        Insets(
          left = max(bodyPadding.calculateLeftPadding(LocalLayoutDirection.current), insets.left),
          top = if (topBar == null) insets.top else bodyPadding.calculateTopPadding(),
          right = max(
            bodyPadding.calculateRightPadding(LocalLayoutDirection.current),
            insets.right
          ),
          bottom = if (bottomBar == null) insets.bottom else bodyPadding.calculateBottomPadding()
        ),
        bodyContent
      )
    }
  }
}

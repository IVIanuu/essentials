/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.ui.insets.Insets
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.insets.LocalInsets

@Composable fun Scaffold(
  modifier: Modifier = Modifier,
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  topBar: (@Composable () -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  floatingActionButton: (@Composable () -> Unit)? = null,
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  isFloatingActionButtonDocked: Boolean = false,
  drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
  drawerShape: Shape = MaterialTheme.shapes.large,
  drawerElevation: Dp = DrawerDefaults.Elevation,
  backgroundColor: Color = MaterialTheme.colors.background,
  applyInsets: Boolean = true,
  content: @Composable () -> Unit
) {
  InsetsPadding(
    left = applyInsets,
    top = false,
    right = applyInsets,
    bottom = false
  ) {
    Scaffold(
      modifier = modifier,
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

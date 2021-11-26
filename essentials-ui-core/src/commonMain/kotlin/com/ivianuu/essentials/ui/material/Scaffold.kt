/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.ui.insets.Insets
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.insets.InsetsProvider
import com.ivianuu.essentials.ui.insets.LocalInsets

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

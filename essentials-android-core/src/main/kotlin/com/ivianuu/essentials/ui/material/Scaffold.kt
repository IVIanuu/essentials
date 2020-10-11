/*
 * Copyright 2019 Manuel Wrage
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerConstants
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.orEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.ProvideInsets
import com.ivianuu.essentials.ui.core.currentInsets

@Composable
fun Scaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerConstants.DefaultElevation,
    backgroundColor: Color = MaterialTheme.colors.background,
    applyInsets: Boolean = true,
    bodyContent: @Composable () -> Unit
) {
    InsetsPadding(
        start = applyInsets,
        top = false,
        end = applyInsets,
        bottom = false
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = topBar.orEmpty(),
            bottomBar = bottomBar.orEmpty(),
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
                    ) else emptyContent(),
            floatingActionButtonPosition = floatingActionButtonPosition,
            isFloatingActionButtonDocked = isFloatingActionButtonDocked,
            drawerContent = drawerContent,
            drawerShape = drawerShape,
            drawerElevation = drawerElevation,
            backgroundColor = backgroundColor
        ) { bodyPadding ->
            val insets = if (applyInsets) currentInsets() else PaddingValues()
            ProvideInsets(
                PaddingValues(
                    start = max(bodyPadding.start, insets.start),
                    top = if (topBar == null) insets.top else bodyPadding.top,
                    end = max(bodyPadding.end, insets.end),
                    bottom = if (bottomBar == null) insets.bottom else bodyPadding.bottom
                ),
                bodyContent
            )
        }
    }
}

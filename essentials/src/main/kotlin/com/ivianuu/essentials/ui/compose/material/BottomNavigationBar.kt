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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.material.surface.CurrentBackground
import androidx.ui.material.surface.Surface
import androidx.ui.material.textColorForBackground
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun BottomNavigationBar(
    length: Int,
    selectedIndex: Int,
    color: Color = +themeColor { primary },
    item: @Composable() (index: Int, selected: Boolean) -> Unit
) = composable("BottomNavigationBar") {
    Surface(color = color, elevation = BottomNavigationBarElevation) {
        Container(height = BottomNavigationBarHeight) {
            Row {
                (0..selectedIndex).forEach { i ->
                    item(i, i == selectedIndex)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBarItem(
    selected: Boolean,
    icon: @Composable() () -> Unit,
    title: @Composable() () -> Unit
) = composable("BottomNavigationBarItem") {
    BottomNavigationBarItem(selected = selected) {
        Column(
            mainAxisAlignment = MainAxisAlignment.Center,
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            icon()
            title()
        }
    }
}

@Composable
fun BottomNavigationBarItem(
    selected: Boolean,
    content: @Composable() () -> Unit
) = composable("BottomNavigationBarItem") {
    Container(
        constraints = DpConstraints(
            minWidth = BottomNavigationBarItemMinWidth,
            maxWidth = BottomNavigationBarItemMaxWidth
        ),
        padding = EdgeInsets(
            left = BottomNavigationBarItemPaddingSide,
            top = BottomNavigationBarItemPaddingTop,
            right = BottomNavigationBarItemPaddingSide,
            bottom = BottomNavigationBarItemPaddingBottom
        )
    ) {
        val backgroundColor = +ambient(CurrentBackground)
        val textStyle = (+themeTextStyle { caption }).copy(
            color = (+textColorForBackground(backgroundColor))!!.copy(
                alpha = if (selected) 1f else 0.38f
            )
        )

        CurrentTextStyleProvider(textStyle) {
            content()
        }
    }
}

private val BottomNavigationBarHeight = 56.dp
private val BottomNavigationBarElevation = 8.dp

private val BottomNavigationBarItemMinWidth = 80.dp
private val BottomNavigationBarItemMaxWidth = 168.dp
private val BottomNavigationBarItemPaddingTop = 8.dp
private val BottomNavigationBarItemPaddingSide = 12.dp
private val BottomNavigationBarItemPaddingBottom = 12.dp
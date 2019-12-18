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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.ambient
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.core.WithConstraints
import androidx.ui.core.coerceIn
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.common.withDensity
import com.ivianuu.essentials.ui.core.Unstable
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.Swapper
import com.ivianuu.essentials.ui.layout.SwapperController

@Unstable
@Composable
fun <T> BottomNavigationBar(
    color: Color = MaterialTheme.colors().primary,
    item: @Composable() (Int, T) -> Unit
) {
    val bottomNavigationController = ambientBottomNavigationController<T>()
    BottomNavigationBar(
        items = bottomNavigationController.items,
        selectedIndex = bottomNavigationController.selectedIndex,
        color = color,
        item = item
    )
}

@Unstable
@Composable
fun <T> BottomNavigationBar(
    items: List<T>,
    selectedIndex: Int,
    color: Color = MaterialTheme.colors().primary,
    item: @Composable() (Int, T) -> Unit
) {
    EsSurface(color = color, elevation = BottomNavigationBarElevation) {
        Container(height = BottomNavigationBarHeight, expanded = true) {
            WithConstraints { thisConstraints ->
                Row(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val itemWidth = (thisConstraints.maxWidth / items.size)
                    val itemConstraints = withDensity {
                        DpConstraints(
                            minWidth = BottomNavigationBarItemMinWidth,
                            maxWidth = itemWidth.toDp().coerceIn(
                                BottomNavigationBarItemMinWidth,
                                BottomNavigationBarItemMaxWidth
                            ),
                            minHeight = thisConstraints.maxHeight.toDp(),
                            maxHeight = thisConstraints.maxHeight.toDp()
                        )
                    }

                    items.forEachIndexed { index, _item ->
                        key(index) {
                            ConstrainedBox(constraints = itemConstraints) {
                                BottomNavigationItemIndexAmbient.Provider(index) {
                                    item(index, _item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBarItem(
    text: String,
    icon: Image
) {
    val bottomNavigationController = ambientBottomNavigationController<Any?>()
    val index = ambient(BottomNavigationItemIndexAmbient)

    BottomNavigationBarItem(
        selected = bottomNavigationController.selectedIndex == index,
        onSelected = { bottomNavigationController.selectedIndex = index },
        text = text,
        icon = icon
    )
}

@Composable
fun BottomNavigationBarItem(
    selected: Boolean,
    onSelected: (() -> Unit)? = null,
    text: String,
    icon: Image
) {
    Ripple(false, radius = BottomNavigationBarItemRippleRadius) {
        Clickable(onClick = onSelected) {
            Container(
                padding = EdgeInsets(
                    left = BottomNavigationBarItemPaddingSide,
                    top = BottomNavigationBarItemPaddingTop,
                    right = BottomNavigationBarItemPaddingSide,
                    bottom = BottomNavigationBarItemPaddingBottom
                )
            ) {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val tint = contentColor().copy(
                        alpha = if (selected) 1f else 0.6f
                    )
                    val iconStyle = currentIconStyle().copy(color = tint)
                    Icon(image = icon, style = iconStyle)

                    val textStyle = MaterialTheme.typography().caption.copy(color = tint)
                    Text(text = text, style = textStyle)
                }
            }
        }
    }
}

@Composable
fun <T> BottomNavigationSwapper(
    keepState: Boolean = false,
    content: @Composable() (Int, T) -> Unit
) {
    val bottomNavigationController = ambientBottomNavigationController<T>()
    val swapperController = remember {
        SwapperController(
            initial = bottomNavigationController.selectedItem,
            keepState = keepState
        )
    }

    remember(bottomNavigationController.selectedItem) {
        swapperController.current = bottomNavigationController.selectedItem
    }

    Swapper(controller = swapperController) {
        content(
            bottomNavigationController.selectedIndex,
            bottomNavigationController.selectedItem
        )
    }
}

private val BottomNavigationBarHeight = 56.dp
private val BottomNavigationBarElevation = 8.dp

private val BottomNavigationBarItemMinWidth = 80.dp
private val BottomNavigationBarItemMaxWidth = 168.dp
private val BottomNavigationBarItemPaddingTop = 8.dp
private val BottomNavigationBarItemPaddingSide = 12.dp
private val BottomNavigationBarItemPaddingBottom = 12.dp
private val BottomNavigationBarItemRippleRadius = 56.dp

fun <T> BottomNavigationController(
    items: List<T>,
    initialIndex: Int = 0,
    children: @Composable() () -> Unit
) {
    val selectedIndex = state { initialIndex }
    val bottomNavigationController = remember { BottomNavigationController(items, selectedIndex) }
    bottomNavigationController.items = items
    BottomNavigationControllerAmbient.Provider(bottomNavigationController, children)
}

@Composable
fun <T> ambientBottomNavigationController(): BottomNavigationController<T> =
    ambient(BottomNavigationControllerAmbient) as BottomNavigationController<T>

class BottomNavigationController<T>(
    var items: List<T>,
    _selectedIndex: State<Int>
) {
    var selectedIndex by _selectedIndex
    val selectedItem: T get() = items[selectedIndex]
}

private val BottomNavigationControllerAmbient = Ambient.of<BottomNavigationController<*>>()

val BottomNavigationItemIndexAmbient = Ambient.of<Int>()

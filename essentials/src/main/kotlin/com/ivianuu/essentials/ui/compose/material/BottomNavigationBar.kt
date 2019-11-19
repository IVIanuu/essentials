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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.State
import androidx.ui.core.Text
import androidx.ui.core.WithConstraints
import androidx.ui.core.coerceIn
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.core.withDensity
import com.ivianuu.essentials.ui.compose.layout.Swapper
import com.ivianuu.essentials.ui.compose.layout.SwapperController

@Composable
fun <T> BottomNavigationBar(
    color: Color = MaterialTheme.colors()().primary,
    item: @Composable() (Int, T) -> Unit
) = composable {
    val bottomNavigationController = ambientBottomNavigationController<T>()
    BottomNavigationBar(
        items = bottomNavigationController.items,
        selectedIndex = bottomNavigationController.selectedIndex,
        color = color,
        item = item
    )
}

@Composable
fun <T> BottomNavigationBar(
    items: List<T>,
    selectedIndex: Int,
    color: Color = MaterialTheme.colors()().primary,
    item: @Composable() (Int, T) -> Unit
) = composable {
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

                    items.forEachIndexed { index, item ->
                        composable(index) {
                            ConstrainedBox(constraints = itemConstraints) {
                                BottomNavigationItemIndexAmbient.Provider(index) {
                                    item(index, item)
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
) = composable {
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
) = composable {
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
                    val tint = colorForCurrentBackground().copy(
                        alpha = if (selected) 1f else 0.6f
                    )
                    val iconStyle = currentIconStyle().copy(color = tint)
                    Icon(image = icon, style = iconStyle)

                    val textStyle = MaterialTheme.typography()().caption.copy(color = tint)
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
) = composable {
    val bottomNavigationController = ambientBottomNavigationController<T>()
    val swapperController = memo {
        SwapperController(
            initial = bottomNavigationController.selectedItem,
            keepState = keepState
        )
    }

    memo(bottomNavigationController.selectedItem) {
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
    val bottomNavigationController = memo { BottomNavigationController(items, selectedIndex) }
    bottomNavigationController.items = items
    BottomNavigationControllerAmbient.Provider(bottomNavigationController, children)
}

fun <T> ambientBottomNavigationController(): BottomNavigationController<T> = effect {
    ambient(BottomNavigationControllerAmbient) as BottomNavigationController<T>
}

class BottomNavigationController<T>(
    var items: List<T>,
    _selectedIndex: State<Int>
) {
    var selectedIndex by _selectedIndex
    val selectedItem: T get() = items[selectedIndex]
}

private val BottomNavigationControllerAmbient = Ambient.of<BottomNavigationController<*>>()

val BottomNavigationItemIndexAmbient = Ambient.of<Int>()
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
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.WithConstraints
import androidx.ui.core.coerceIn
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ProvideContentColor
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.common.SafeArea
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.ui.common.withDensity
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.Unstable
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.Swapper
import com.ivianuu.essentials.ui.layout.SwapperState

@Immutable
data class BottomNavigationBarStyle(
    val color: Color,
    val selectedItemColor: Color,
    val normalItemColor: Color
)

val BottomNavigationBarStyleAmbient = Ambient.of<BottomNavigationBarStyle?>()

@Composable
fun DefaultBottomNavigationBarStyle(
    color: Color = MaterialTheme.colors().primary,
    selectedItemColor: Color = MaterialTheme.colors().onPrimary,
    normalItemColor: Color = MaterialTheme.colors().onPrimary.copy(alpha = 0.6f)
) = BottomNavigationBarStyle(
    color = color,
    selectedItemColor = selectedItemColor,
    normalItemColor = normalItemColor
)

@Unstable
@Composable
fun <T> BottomNavigationBar(
    controller: BottomNavigationController<T> = ambientBottomNavigationController(),
    style: BottomNavigationBarStyle = ambient(BottomNavigationBarStyleAmbient) ?: DefaultBottomNavigationBarStyle(),
    applySafeArea: Boolean = true,
    item: @Composable() (T) -> Unit
) {
    Surface(color = style.color, elevation = BottomNavigationBarElevation) {
        SafeArea(
            left = false,
            top = false,
            right = false,
            bottom = applySafeArea
        ) {
            // used to fix that the ripples go beyond the bottom bar bounds
            RepaintBoundary {
                Container(height = BottomNavigationBarHeight, expanded = true) {
                    WithConstraints { thisConstraints ->
                        Row(
                            mainAxisAlignment = MainAxisAlignment.Center,
                            crossAxisAlignment = CrossAxisAlignment.Center
                        ) {
                            val itemWidth = (thisConstraints.maxWidth / controller.items.size)
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

                            controller.items.forEach { _item ->
                                key(_item) {
                                    Container(constraints = itemConstraints) {
                                        ProvideContentColor(
                                            color = if (_item == controller.selectedItem) style.selectedItemColor else style.normalItemColor
                                        ) {
                                            BottomNavigationItemAmbient.Provider(_item) {
                                                item(_item)
                                            }
                                        }
                                    }
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
    onSelected: (() -> Unit)? = defaultOnSelected<Any?>(),
    text: String,
    icon: Image
) {
    Ripple(bounded = false, radius = BottomNavigationBarItemRippleRadius) {
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
                    Icon(image = icon)
                    Text(text = text, style = MaterialTheme.typography().caption)
                }
            }
        }
    }
}

@Composable
private fun <T> defaultOnSelected(): () -> Unit {
    val controller = ambientBottomNavigationController<T>()
    val item = ambientBottomNavigationItem<T>()
    return { controller.selectedItem = item }
}

@Composable
fun <T> BottomNavigationSwapper(
    bottomNavigationController: BottomNavigationController<T> = ambientBottomNavigationController(),
    keepState: Boolean = false,
    content: @Composable() (T) -> Unit
) {
    val swapperController = retain {
        SwapperState(
            initial = bottomNavigationController.selectedItem,
            keepState = keepState
        )
    }
    remember(keepState) {
        swapperController.keepState = keepState
    }

    remember(bottomNavigationController.selectedItem) {
        swapperController.current = bottomNavigationController.selectedItem
    }

    Swapper(controller = swapperController) {
        content(bottomNavigationController.selectedItem)
    }
}

@Composable
fun <T> ProvideBottomNavigationController(
    items: List<T>,
    initial: T = items.first(),
    children: @Composable() () -> Unit
) {
    val controller = retain {
        BottomNavigationController(items = items, initial = initial)
    }
    BottomNavigationControllerAmbient.Provider(value = controller, children = children)
}

private val BottomNavigationControllerAmbient = Ambient.of<BottomNavigationController<*>>()

@Composable
fun <T> ambientBottomNavigationController(): BottomNavigationController<T> = ambient(
    BottomNavigationControllerAmbient) as BottomNavigationController<T>

private val BottomNavigationItemAmbient = Ambient.of<Any?>()

@Composable
fun <T> ambientBottomNavigationItem(): T = ambient(BottomNavigationItemAmbient) as T

private val BottomNavigationBarHeight = 56.dp
private val BottomNavigationBarElevation = 8.dp
private val BottomNavigationBarItemMinWidth = 80.dp
private val BottomNavigationBarItemMaxWidth = 168.dp
private val BottomNavigationBarItemPaddingTop = 8.dp
private val BottomNavigationBarItemPaddingSide = 12.dp
private val BottomNavigationBarItemPaddingBottom = 12.dp
private val BottomNavigationBarItemRippleRadius = 56.dp

class BottomNavigationController<T>(
    items: List<T>,
    initial: T = items.first()
) {
    var items by framed(items)
    var selectedItem by framed(initial)
}
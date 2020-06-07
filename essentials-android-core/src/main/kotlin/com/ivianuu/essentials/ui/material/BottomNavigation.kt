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

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Providers
import androidx.compose.emptyContent
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.graphics.Color
import androidx.ui.layout.RowScope
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.common.Swapper
import com.ivianuu.essentials.ui.common.SwapperState
import com.ivianuu.essentials.ui.core.currentOrElse
import com.ivianuu.essentials.ui.core.rememberRetained

@Immutable
data class BottomNavigationStyle(
    val backgroundColor: Color,
    val activeColor: Color,
    val inactiveColor: Color,
    val alwaysShowLabels: Boolean,
    val elevation: Dp,
    val modifier: Modifier = Modifier
)

val BottomNavigationStyleAmbient = staticAmbientOf<BottomNavigationStyle>()

@Composable
fun DefaultBottomNavigationStyle(
    color: Color = MaterialTheme.colors.primary,
    activeColor: Color = MaterialTheme.colors.onPrimary,
    inactiveColor: Color = MaterialTheme.colors.onPrimary.copy(alpha = 0.6f),
    alwaysShowLabels: Boolean = false,
    elevation: Dp = 8.dp,
    modifier: Modifier = Modifier
) = BottomNavigationStyle(
    backgroundColor = color,
    activeColor = activeColor,
    inactiveColor = inactiveColor,
    alwaysShowLabels = alwaysShowLabels,
    elevation = elevation,
    modifier = modifier
)

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    style: BottomNavigationStyle = BottomNavigationStyleAmbient.currentOrElse { DefaultBottomNavigationStyle() },
    children: @Composable RowScope.() -> Unit
) {
    BottomNavigation(
        modifier = style.modifier + modifier,
        backgroundColor = style.backgroundColor,
        contentColor = style.activeColor,
        elevation = style.elevation
    ) {
        Providers(BottomNavigationStyleAmbient provides style) {
            children()
        }
    }
}

@Composable
fun RowScope.BottomNavigationItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit = emptyContent(),
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    style: BottomNavigationStyle = BottomNavigationStyleAmbient.currentOrElse { DefaultBottomNavigationStyle() }
) {
    BottomNavigationItem(
        icon = icon,
        text = text,
        selected = selected,
        onSelected = onSelected,
        modifier = modifier,
        alwaysShowLabels = style.alwaysShowLabels,
        activeColor = style.activeColor,
        inactiveColor = style.inactiveColor
    )
}

class BottomNavigationController<T>(
    items: List<T>,
    initial: T = items.first()
) {
    var items by mutableStateOf(items)
    var selectedItem by mutableStateOf(initial)
}

@Composable
fun <T> ProvideBottomNavigationController(
    items: List<T>,
    initial: T = items.first(),
    children: @Composable () -> Unit
) {
    val controller = rememberRetained(items, initial) {
        BottomNavigationController(items = items, initial = initial)
    }

    Providers(BottomNavigationControllerAmbient provides controller, children = children)
}

@Composable
fun <T> ProvideBottomNavigationController(
    controller: BottomNavigationController<T>,
    children: @Composable () -> Unit
) {
    Providers(BottomNavigationControllerAmbient provides controller, children = children)
}

private val BottomNavigationControllerAmbient =
    staticAmbientOf<BottomNavigationController<*>> {
        error("No bottom navigation controller found")
    }

@Composable
fun <T> ambientBottomNavigationController(): BottomNavigationController<T> = BottomNavigationControllerAmbient.current as BottomNavigationController<T>

private val BottomNavigationItemAmbient =
    staticAmbientOf<Any?>()

@Composable
fun <T> ambientBottomNavigationItem(): T = BottomNavigationItemAmbient.current as T

@Composable
fun <T> BottomNavigation(
    controller: BottomNavigationController<T> = ambientBottomNavigationController(),
    modifier: Modifier = Modifier,
    style: BottomNavigationStyle = BottomNavigationStyleAmbient.currentOrElse { DefaultBottomNavigationStyle() },
    itemCallback: @Composable RowScope.(T) -> Unit
) {
    BottomNavigation(
        modifier = modifier,
        style = style
    ) {
        controller.items.forEach { item ->
            key(item) {
                Providers(BottomNavigationItemAmbient provides item) {
                    itemCallback(item)
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomNavigationItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit = emptyContent(),
    modifier: Modifier = Modifier,
    style: BottomNavigationStyle = BottomNavigationStyleAmbient.currentOrElse { DefaultBottomNavigationStyle() }
) {
    val controller = ambientBottomNavigationController<Any?>()
    val item = ambientBottomNavigationItem<Any?>()
    BottomNavigationItem(
        icon = icon,
        text = text,
        modifier = modifier,
        style = style,
        selected = controller.selectedItem == item,
        onSelected = { controller.selectedItem = item }
    )
}

@Composable
fun <T> BottomNavigationSwapper(
    bottomNavigationController: BottomNavigationController<T> = ambientBottomNavigationController(),
    keepState: Boolean = false,
    modifier: Modifier = Modifier,
    contentCallback: @Composable (T) -> Unit
) {
    val swapperController = rememberRetained {
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

    Swapper(state = swapperController, modifier = modifier) {
        Providers(BottomNavigationItemAmbient provides bottomNavigationController.selectedItem) {
            contentCallback(bottomNavigationController.selectedItem)
        }
    }
}

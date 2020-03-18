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
import androidx.compose.Model
import androidx.compose.Providers
import androidx.compose.emptyContent
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import com.ivianuu.essentials.ui.common.Swapper
import com.ivianuu.essentials.ui.common.SwapperState
import com.ivianuu.essentials.ui.core.DefaultTextComposableStyle
import com.ivianuu.essentials.ui.core.TextComposableStyleAmbient
import com.ivianuu.essentials.ui.core.retain

@Model
class TabController<T>(
    var items: List<T>,
    initialIndex: Int = 0
) {
    var selectedIndex = initialIndex
    val selectedItem: T get() = items[selectedIndex]
}

private val TabControllerAmbient =
    staticAmbientOf<TabController<*>>()

@Composable
fun <T> ambientTabController(): TabController<T> = TabControllerAmbient.current as TabController<T>

@Composable
fun <T> ProvideTabController(
    items: List<T>,
    initialIndex: Int = 0,
    children: @Composable () -> Unit
) {
    val controller = retain(items, initialIndex) {
        TabController(items = items, initialIndex = initialIndex)
    }

    Providers(TabControllerAmbient provides controller, children = children)
}

@Composable
fun <T> ProvideTabController(
    tabController: TabController<T>,
    children: @Composable () -> Unit
) {
    Providers(TabControllerAmbient provides tabController, children = children)
}

@Composable
fun <T> TabRow(
    tabController: TabController<T> = ambientTabController(),
    scrollable: Boolean = false,
    indicatorContainer: @Composable (tabPositions: List<TabRow.TabPosition>) -> Unit = { tabPositions ->
        TabRow.IndicatorContainer(tabPositions, tabController.selectedIndex) {
            TabRow.Indicator()
        }
    },
    tab: @Composable (Int, T) -> Unit
) {
    TabRow(
        items = tabController.items,
        selectedIndex = tabController.selectedIndex,
        scrollable = scrollable,
        indicatorContainer = indicatorContainer,
        tab = { index, item ->
            key(item as Any) {
                Providers(TabIndexAmbient provides index) {
                    tab(index, item)
                }
            }
        }
    )
}

val TabIndexAmbient =
    staticAmbientOf<Int>()

@Composable
fun Tab(
    text: @Composable() () -> Unit = emptyContent(),
    icon: @Composable() () -> Unit = emptyContent()
) {
    val tabController = ambientTabController<Any?>()
    val tabIndex = TabIndexAmbient.current
    Tab(
        text = {
            Providers(
                TextComposableStyleAmbient provides DefaultTextComposableStyle(
                    uppercase = true,
                    maxLines = 1
                ),
                children = text
            )
        },
        icon = icon,
        selected = tabController.selectedIndex == tabIndex,
        onSelected = { tabController.selectedIndex = tabIndex }
    )
}

@Composable
fun <T> TabContent(
    tabController: TabController<T> = ambientTabController(),
    keepState: Boolean = false,
    item: @Composable (Int, T) -> Unit
) {
    val swapperController = remember {
        SwapperState(
            tabController.selectedItem
        )
    }
    remember(keepState) { swapperController.keepState = keepState }
    remember(tabController.selectedItem) { swapperController.current = tabController.selectedItem }
    Swapper(
        state = swapperController
    ) { item(tabController.selectedIndex, tabController.selectedItem) }
}

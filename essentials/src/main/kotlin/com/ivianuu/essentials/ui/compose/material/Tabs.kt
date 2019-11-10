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
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.onCommit
import androidx.compose.unaryPlus
import androidx.ui.graphics.Image
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import com.ivianuu.essentials.ui.compose.common.Pager
import com.ivianuu.essentials.ui.compose.common.PagerPosition
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable

fun <T> TabController(
    items: List<T>,
    initialIndex: Int = 0,
    children: @Composable() () -> Unit
) {
    val tabController = +memo { TabController(items, initialIndex) }
    tabController.items = items
    TabControllerAmbient.Provider(tabController, children)
}

fun <T> ambientTabController() = effectOf<TabController<T>> {
    +ambient(TabControllerAmbient) as TabController<T>
}

class TabController<T>(
    var items: List<T>,
    initialIndex: Int
) {
    var selectedIndex by framed(initialIndex)
    val selectedItem: T get() = items[selectedIndex]
}

private val TabControllerAmbient = Ambient.of<TabController<*>>()

@Composable
fun <T> TabRow(
    tabController: TabController<T> = +ambientTabController<T>(),
    scrollable: Boolean = false,
    indicatorContainer: @Composable() (tabPositions: List<TabRow.TabPosition>) -> Unit = { tabPositions ->
        TabRow.IndicatorContainer(tabPositions, tabController.selectedIndex) {
            TabRow.Indicator()
        }
    },
    tab: @Composable() (Int, T) -> Unit
) = composable("TabRow") {
    TabRow(
        items = tabController.items,
        selectedIndex = tabController.selectedIndex,
        scrollable = scrollable,
        indicatorContainer = indicatorContainer,
        tab = { index, item ->
            TabIndexAmbient.Provider(index) {
                tab(index, item)
            }
        }
    )
}

val TabIndexAmbient = Ambient.of<Int>()

@Composable
fun Tab(
    text: String? = null,
    icon: Image? = null
) = composable("Tab") {
    val tabController = +ambientTabController<Any?>()
    val tabIndex = +ambient(TabIndexAmbient)
    Tab(
        text = text,
        icon = icon,
        selected = tabController.selectedIndex == tabIndex,
        onSelected = { tabController.selectedIndex = tabIndex }
    )
}

@Composable
fun <T> TabPager(
    tabController: TabController<T> = +ambientTabController<T>(),
    item: @Composable() (Int, T) -> Unit
) = composable("TabPager") {
    val position = +memo { PagerPosition(tabController.items.size) }

    +onCommit(tabController.selectedIndex) {
        position.animateToPage(tabController.selectedIndex)
    }

    TabIndexAmbient.Provider(tabController.selectedIndex) {
        Pager(
            position = position,
            items = tabController.items,
            onPageChanged = { tabController.selectedIndex = it },
            direction = Axis.Horizontal,
            item = item
        )
    }
}
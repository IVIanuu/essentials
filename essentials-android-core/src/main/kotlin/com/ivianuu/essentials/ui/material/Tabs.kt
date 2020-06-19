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
import androidx.compose.Providers
import androidx.compose.emptyContent
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.contentColorFor
import androidx.ui.material.primarySurface
import androidx.ui.savedinstancestate.rememberSavedInstanceState
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
class TabController<T>(
    items: List<T>,
    initialIndex: Int = 0
) {
    var items by mutableStateOf(items)
    var selectedIndex by mutableStateOf(initialIndex)
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
    val controller = rememberSavedInstanceState(items) {
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
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    scrollable: Boolean = false,
    indicatorContainer: @Composable (tabPositions: List<TabRow.TabPosition>) -> Unit = { tabPositions ->
        TabRow.IndicatorContainer(tabPositions, tabController.selectedIndex) {
            TabRow.Indicator()
        }
    },
    divider: @Composable () -> Unit = {
        Divider(thickness = 1.dp, color = contentColor().copy(alpha = 0.12f))
    },
    tab: @Composable (Int, T) -> Unit
) {
    TabRow(
        items = tabController.items,
        selectedIndex = tabController.selectedIndex,
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        scrollable = scrollable,
        indicatorContainer = indicatorContainer,
        divider = divider
    ) { index, item ->
        key(item as Any) {
            Providers(TabIndexAmbient provides index) {
                tab(index, item)
            }
        }
    }
}

val TabIndexAmbient =
    staticAmbientOf<Int>()

@Composable
fun Tab(
    text: @Composable () -> Unit = emptyContent(),
    icon: @Composable () -> Unit = emptyContent(),
    modifier: Modifier = Modifier,
    activeColor: Color = contentColor(),
    inactiveColor: Color = EmphasisAmbient.current.medium.applyEmphasis(activeColor)
) {
    val tabController = ambientTabController<Any?>()
    val tabIndex = TabIndexAmbient.current
    Tab(
        text = text,
        icon = icon,
        selected = tabController.selectedIndex == tabIndex,
        onSelected = { tabController.selectedIndex = tabIndex },
        modifier = modifier,
        activeColor = activeColor,
        inactiveColor = inactiveColor
    )
}

@Composable
fun <T> TabContent(
    modifier: Modifier = Modifier,
    tabController: TabController<T> = ambientTabController(),
    transition: StackTransition = FadeStackTransition(),
    item: @Composable (T) -> Unit
) {
    AnimatedBox(
        modifier = modifier,
        current = tabController.selectedItem,
        transition = transition
    ) { item ->
        Providers(TabIndexAmbient provides tabController.items.indexOf(item)) {
            item(item)
        }
    }
}

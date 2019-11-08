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
import androidx.compose.unaryPlus
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun <T> TabContent(
    tabController: TabController<T> = +ambientTabController<T>(),
    item: @Composable() (Int, T) -> Unit
) = TabContent(
    items = tabController.items,
    selectedIndex = tabController.selectedIndex,
    item = item
)

@Composable
fun <T> TabContent(
    items: List<T>,
    selectedIndex: Int,
    item: @Composable() (Int, T) -> Unit
) = composable("TabContent") {
    SimpleTabContent(items, selectedIndex, item)
}

@Composable
private fun <T> SimpleTabContent(
    items: List<T>,
    selectedIndex: Int,
    item: @Composable() (Int, T) -> Unit
) = composable("SimpleTabContent") {
    TabIndexAmbient.Provider(selectedIndex) {
        item(selectedIndex, items[selectedIndex])
    }
}

@Composable
private fun <T> DraggableTabContent(
    items: List<T>,
    selectedIndex: Int,
    item: @Composable() (Int, T) -> Unit
) = composable("DraggableTabContent") {
    Layout({
        items.forEachIndexed { index, item ->
            composable(index) {
                item(index, item)
            }
        }
    }) { measureables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = constraints.maxHeight
        )

        val placeables = measureables.map { it.measure(childConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            var offset = -(constraints.maxWidth * selectedIndex)
            placeables.forEach {
                it.place(offset, IntPx.Zero)
                offset += constraints.maxWidth
            }
        }
    }
}
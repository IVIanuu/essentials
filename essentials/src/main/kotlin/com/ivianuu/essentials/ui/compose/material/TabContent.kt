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
import com.ivianuu.essentials.ui.compose.common.Pager
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun <T> TabPager(
    tabController: TabController<T> = +ambientTabController<T>(),
    item: @Composable() (Int, T) -> Unit
) = composable("TabPager") {
    TabIndexAmbient.Provider(tabController.selectedIndex) {
        Pager(
            items = tabController.items,
            currentPage = tabController.selectedIndex,
            direction = Axis.Horizontal,
            item = item
        )
    }
}
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

package com.ivianuu.essentials.sample.ui

import androidx.ui.core.Text
import androidx.ui.core.dp
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.SliverList
import com.ivianuu.essentials.ui.compose.common.scrolling.sliver.SliverScroller
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.SimpleListItem

val sliverRoute = composeControllerRoute {
    SliverScroller {
        SliverList(
            count = 100,
            itemSize = 48.dp,
            item = {
                SimpleListItem(title = { Text("Item ${it + 1}") })
            }
        )
    }
}
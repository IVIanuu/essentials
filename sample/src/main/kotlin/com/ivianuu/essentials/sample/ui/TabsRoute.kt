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
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.Tab
import com.ivianuu.essentials.ui.compose.material.TabContent
import com.ivianuu.essentials.ui.compose.material.TabController
import com.ivianuu.essentials.ui.compose.material.TabRow
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.vertical

val tabsRoute = composeControllerRoute(
    options = controllerRouteOptions().vertical()
) {
    val items = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)

    TabController(items = items) {
        Scaffold(
            topAppBar = {
                Column {
                    EsTopAppBar("Tabs")
                    TabRow<Color> { index, item ->
                        Tab(text = "Item: $index")
                    }
                }
            },
            body = {
                TabContent<Color> { index, item ->
                    Surface(color = item) {
                        Container(expanded = true) {
                            Text("Index: $index")
                        }
                    }
                }
            }
        )
    }
}
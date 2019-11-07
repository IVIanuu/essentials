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

import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.layout.Center
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.BottomNavigationBar
import com.ivianuu.essentials.ui.compose.material.BottomNavigationBarItem
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

val bottomNavigationRoute = composeControllerRoute(
    options = controllerRouteOptions().fade()
) {
    val (selectedIndex, setSelectedIndex) = +state { 0 }
    d { "compose with selected index $selectedIndex" }

    Scaffold(
        topAppBar = { EsTopAppBar("Bottom navigation") },
        content = {
            Center {
                Text("Selected $selectedIndex")
            }
        },
        bottomBar = {
            BottomNavigationBar(
                length = 3,
                selectedIndex = selectedIndex
            ) { index, selected ->
                BottomNavigationBarItem(
                    selected = selected,
                    onClick = { setSelectedIndex(index) },
                    icon = { Icon(+drawableResource(R.drawable.es_ic_link)) },
                    title = { Text("Index: $index") }
                )
            }
        }
    )
}
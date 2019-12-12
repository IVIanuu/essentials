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
import androidx.ui.layout.Center
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.surface.Surface
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.navigation.director.ControllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

val DrawerRoute = ComposeControllerRoute(
    options = ControllerRouteOptions().fade()
) {
    Scaffold(
        topAppBar = { EsTopAppBar("Drawer") },
        drawer = { state, onStateChanged, body ->
            ModalDrawerLayout(
                drawerState = state,
                onStateChange = onStateChanged,
                bodyContent = body,
                drawerContent = {
                    Surface(color = Color.Blue) {
                        Center {
                            Text(
                                text = "Drawer",
                                style = MaterialTheme.typography()().h4
                            )
                        }
                    }
                }
            )
        },
        body = {
            Surface(color = Color.Red) {
                Center {
                    Text(
                        text = "Body",
                        style = MaterialTheme.typography()().h4
                    )
                }
            }
        }
    )
}
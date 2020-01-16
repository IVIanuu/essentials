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

import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.transition.FadeRouteTransition

val DrawerRoute = Route(enterTransition = FadeRouteTransition()) {
    Scaffold(
        topAppBar = { TopAppBar("Drawer") },
        drawerContent = {
            Surface(color = Color.Blue) {
                Center {
                    Text(
                        text = "Drawer",
                        style = MaterialTheme.typography().h4
                    )
                }
            }
        },
        body = {
            Surface(color = Color.Red) {
                Center {
                    Text(
                        text = "Body",
                        style = MaterialTheme.typography().h4
                    )
                }
            }
        }
    )
}
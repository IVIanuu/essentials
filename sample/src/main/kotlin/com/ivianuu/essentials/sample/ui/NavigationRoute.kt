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

import androidx.compose.remember
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.HeightSpacer
import com.ivianuu.essentials.ui.compose.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.compose.material.EsSurface
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.navigation.FadeRouteTransition
import com.ivianuu.essentials.ui.compose.navigation.Navigator
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.ui.compose.navigation.VerticalRouteTransition
import com.ivianuu.essentials.ui.compose.navigation.navigator
import kotlin.time.milliseconds

val navigationRoute =
    ComposeControllerRoute {
        Navigator(
            startRoute = remember { CounterRoute(1) }
        )
    }

private val colors = listOf(
    Color.Red,
    Color.Blue,
    Color.Gray,
    Color.Cyan
)

private val transitions = listOf(
    VerticalRouteTransition(300.milliseconds),
    FadeRouteTransition(300.milliseconds)
)

private fun CounterRoute(count: Int): Route = Route(
    name = "Count: $count",
    keepState = true,
    transition = transitions.shuffled().first()
) {
    Scaffold(
        topAppBar = { EsTopAppBar("Nav") },
        body = {
            EsSurface(color = remember { colors.shuffled().first() }) {
                Center {
                    Column(
                        mainAxisAlignment = MainAxisAlignment.Center,
                        crossAxisAlignment = CrossAxisAlignment.Center
                    ) {
                        val navigator = navigator

                        Text(
                            "Count: $count",
                            style = MaterialTheme.typography().h1
                        )

                        HeightSpacer(8.dp)

                        Button(
                            text = "Next",
                            onClick = { navigator.push(CounterRoute(count + 1)) }
                        )

                        HeightSpacer(8.dp)

                        Button(
                            text = "Previous",
                            onClick = { navigator.pop() }
                        )
                    }
                }
            }
        }
    )
}
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
import androidx.ui.layout.Center
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.HeightSpacer
import com.ivianuu.essentials.ui.compose.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.navigation.ComposeNavigator
import com.ivianuu.essentials.ui.compose.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.compose.navigation.Route

val navigationRoute =
    ComposeControllerRoute {
        ComposeNavigator { CounterRoute(1) }
    }

private fun CounterRoute(count: Int): Route = Route {
    Scaffold(
        topAppBar = { EsTopAppBar("Nav") },
        body = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val navigator = ambient(NavigatorAmbient)

                    Text(
                        "Count: $count",
                        style = MaterialTheme.typography()().h1
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
    )
}
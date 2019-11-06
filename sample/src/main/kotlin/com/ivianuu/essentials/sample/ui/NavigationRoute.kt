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

import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.Button
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.navigation.Navigator
import com.ivianuu.essentials.ui.compose.navigation.NavigatorAmbient
import com.ivianuu.essentials.ui.compose.navigation.Route

val navigationRoute = composeControllerRoute {
    Navigator { CounterRoute(1) }
}

private fun CounterRoute(count: Int): Route = Route(count) {
    Scaffold(
        appBar = { EsTopAppBar("Nav") },
        content = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val navigator = +ambient(NavigatorAmbient)

                    Text(
                        "Count: $count",
                        style = +themeTextStyle { h1 }
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
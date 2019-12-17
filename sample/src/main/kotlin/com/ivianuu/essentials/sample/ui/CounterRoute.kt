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
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Center
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.compose.layout.HeightSpacer
import com.ivianuu.essentials.ui.compose.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.navigation.Route

val CounterRoute = Route {
    Scaffold(
        topAppBar = { EsTopAppBar("Counter") },
        body = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val (count, setCount) = state { 0 }

                    Text(
                        text = "Count: $count",
                        style = MaterialTheme.typography().h3
                    )

                    HeightSpacer(8.dp)

                    FloatingActionButton(
                        text = "Inc",
                        onClick = { setCount(count + 1) }
                    )

                    HeightSpacer(8.dp)

                    FloatingActionButton(
                        text = "dec",
                        onClick = { setCount(count - 1) }
                    )
                }
            }
        }
    )
}
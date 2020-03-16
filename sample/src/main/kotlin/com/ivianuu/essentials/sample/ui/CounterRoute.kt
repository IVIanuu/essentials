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
import androidx.ui.layout.Center
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentTextComposableStyle
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route

val CounterRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Counter") }) },
        body = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val (count, setCount) = state { 0 }

                    Text(
                        text = "Count: $count",
                        style = currentTextComposableStyle()
                            .copy(textStyle = MaterialTheme.typography().h3)
                    )

                    Spacer(LayoutHeight(8.dp))

                    FloatingActionButton(
                        text = { Text("Inc") },
                        onClick = { setCount(count + 1) }
                    )

                    Spacer(LayoutHeight(8.dp))

                    FloatingActionButton(
                        text = { Text("dec") },
                        onClick = { setCount(count - 1) }
                    )
                }
            }
        }
    )
}
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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.FloatingActionButton
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route

val CounterRoute = Route {
    Scaffold(
        topAppBar = { TopAppBar(title = { Text("Counter") }) },
        body = {
            Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalGravity = Alignment.CenterHorizontally
                ) {
                    val (count, setCount) = state { 0 }

                    Text(
                        text = "Count: $count",
                        textStyle = MaterialTheme.typography.h3
                    )

                    Spacer(Modifier.preferredHeight(8.dp))

                    FloatingActionButton(
                        text = { Text("Inc") },
                        onClick = { setCount(count + 1) }
                    )

                    Spacer(Modifier.preferredHeight(8.dp))

                    FloatingActionButton(
                        text = { Text("dec") },
                        onClick = { setCount(count - 1) }
                    )
                }
            }
        }
    )
}
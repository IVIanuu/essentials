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

import androidx.ui.core.drawShadow
import androidx.ui.foundation.Box
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.unit.dp
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.layout.Column
import com.ivianuu.essentials.android.ui.material.ProvideTabController
import com.ivianuu.essentials.android.ui.material.Scaffold
import com.ivianuu.essentials.android.ui.material.Surface
import com.ivianuu.essentials.android.ui.material.Tab
import com.ivianuu.essentials.android.ui.material.TabContent
import com.ivianuu.essentials.android.ui.material.TabRow
import com.ivianuu.essentials.android.ui.material.TopAppBar
import com.ivianuu.essentials.android.ui.navigation.Route

val TabsRoute = Route {
    val items = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)

    ProvideTabController(items = items) {
        Scaffold(
            topAppBar = {
                Column {
                    TopAppBar("Tabs")
                    Box(
                        modifier = drawShadow(shape = RectangleShape, elevation = 8.dp)
                    ) {
                        TabRow<Color> { index, item ->
                            Tab(text = { Text("Item: $index") })
                        }
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

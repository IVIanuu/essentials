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

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ProvideTabController
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.Tab
import com.ivianuu.essentials.ui.material.TabContent
import com.ivianuu.essentials.ui.material.TabRow
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class TabsPage {

    @Composable
    operator fun invoke() {
        val items = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)

        ProvideTabController(items = items) {
            Scaffold(
                topBar = {
                    Surface(
                        color = MaterialTheme.colors.primary,
                        elevation = 8.dp
                    ) {
                        Column {
                            TopAppBar(
                                title = { Text("Tabs") },
                                elevation = 0.dp
                            )
                            TabRow<Color> { index, item ->
                                Tab(text = { Text("Item: $index") })
                            }
                        }
                    }
                }
            ) {
                TabContent<Color> { item ->
                    Surface(color = item) {
                        Text(
                            text = "Index: ${items.indexOf(item)}",
                            modifier = Modifier.center()
                        )
                    }
                }
            }
        }
    }

}

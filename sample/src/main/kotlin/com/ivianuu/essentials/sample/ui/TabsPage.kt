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
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class TabsPage {

    @Composable
    operator fun invoke() {
        var selectedIndex by savedInstanceState { 0 }
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
                        TabRow(items = TabItems, selectedIndex = selectedIndex) { index, _ ->
                            Tab(
                                selected = selectedIndex == index,
                                onSelected = { selectedIndex = index },
                                text = { Text("Item: $index") }
                            )
                        }
                    }
                }
            }
        ) {
            AnimatedBox(current = TabItems[selectedIndex]) { item ->
                Surface(color = item) {
                    Text(
                        text = "Index: ${TabItems.indexOf(item)}",
                        modifier = Modifier.center()
                    )
                }
            }
        }
    }

}

private val TabItems = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)

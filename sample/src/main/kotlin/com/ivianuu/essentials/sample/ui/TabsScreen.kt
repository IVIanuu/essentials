/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun

@HomeItemBinding
@Given
val tabsHomeItem = HomeItem("Tabs") { TabsKey() }

class TabsKey

@KeyUiBinding<TabsKey>
@GivenFun
@Composable
fun TabsScreen() {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
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
                    TabRow(selectedTabIndex = selectedIndex) {
                        TabItems.forEachIndexed { index, color ->
                            Tab(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                text = { Text("Item: $index") }
                            )
                        }
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

private val TabItems = listOf(Color.Blue, Color.Red, Color.Magenta, Color.Green, Color.Cyan)

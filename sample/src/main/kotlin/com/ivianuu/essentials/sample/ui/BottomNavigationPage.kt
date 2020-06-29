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
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Email
import androidx.ui.material.icons.filled.Home
import androidx.ui.material.icons.filled.Search
import androidx.ui.material.icons.filled.Settings
import androidx.ui.material.icons.filled.ViewAgenda
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.core.SystemBarsPadding
import com.ivianuu.essentials.ui.core.systemBarOverlayStyle
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.isLight
import com.ivianuu.injekt.Transient

@Transient
class BottomNavigationPage {
    @Composable
    operator fun invoke() {
        var selectedItem by savedInstanceState { BottomNavItem.values().first() }

        Scaffold(
            topBar = { TopAppBar(title = { Text("Bottom navigation") }) },
            bottomBar = {
                Surface(
                    modifier = Modifier.systemBarOverlayStyle(MaterialTheme.colors.primary.isLight),
                    elevation = 8.dp,
                    color = MaterialTheme.colors.primary
                ) {
                    SystemBarsPadding(left = false, top = false, right = false) {
                        BottomNavigation(
                            backgroundColor = MaterialTheme.colors.primary,
                            elevation = 0.dp
                        ) {
                            BottomNavItem.values().forEach { item ->
                                BottomNavigationItem(
                                    selected = item == selectedItem,
                                    onSelected = { selectedItem = item },
                                    icon = { Icon(item.icon) },
                                    text = { Text(item.title) }
                                )
                            }
                        }
                    }
                }
            }
        ) {
            AnimatedBox(current = selectedItem) { item ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = item.color
                )
            }
        }
    }
}


private enum class BottomNavItem(
    val title: String,
    val icon: VectorAsset,
    val color: Color
) {
    Home(
        title = "Home",
        icon = Icons.Default.Home,
        color = Color.Yellow
    ),
    Mails(
        title = "Mails",
        icon = Icons.Default.Email,
        color = Color.Red
    ),
    Search(
        title = "Search",
        icon = Icons.Default.Search,
        color = Color.Blue
    ),
    Schedule(
        title = "Schedule",
        icon = Icons.Default.ViewAgenda,
        color = Color.Cyan
    ),
    Settings(
        title = "Settings",
        icon = Icons.Default.Settings,
        color = Color.Green
    )
}
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

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.savedinstancestate.savedInstanceState
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.core.SystemBarsPadding
import com.ivianuu.essentials.ui.core.overlaySystemBarBgColor
import com.ivianuu.essentials.ui.core.systemBarStyle
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.isLight

@Composable
fun BottomNavigationPage() {
    var selectedItem by savedInstanceState { BottomNavItem.values().first() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bottom navigation") }) },
        bottomBar = {
            Surface(
                modifier = Modifier.systemBarStyle(
                    bgColor = overlaySystemBarBgColor(MaterialTheme.colors.primary),
                    lightIcons = MaterialTheme.colors.primary.isLight
                ),
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
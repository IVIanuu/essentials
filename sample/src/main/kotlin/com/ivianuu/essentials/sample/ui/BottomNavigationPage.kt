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
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Email
import androidx.ui.material.icons.filled.Home
import androidx.ui.material.icons.filled.Search
import androidx.ui.material.icons.filled.Settings
import androidx.ui.material.icons.filled.ViewAgenda
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.material.BottomNavigation
import com.ivianuu.essentials.ui.material.BottomNavigationContent
import com.ivianuu.essentials.ui.material.BottomNavigationItem
import com.ivianuu.essentials.ui.material.ProvideBottomNavigationController
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class BottomNavigationPage {
    @Composable
    operator fun invoke() {
        ProvideBottomNavigationController(items = BottomNavItem.values().toList()) {
            Scaffold(
                topBar = { TopAppBar(title = { Text("Bottom navigation") }) },
                bottomBar = {
                    BottomNavigation<BottomNavItem> { item ->
                        BottomNavigationItem(
                            icon = { Icon(item.icon) },
                            text = { Text(item.title) }
                        )
                    }
                }
            ) {
                BottomNavigationContent<BottomNavItem> { item ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = item.color
                    )
                }
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
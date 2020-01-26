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

import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.gestures.action.actions.Settings
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.Email
import com.ivianuu.essentials.material.icons.filled.Home
import com.ivianuu.essentials.material.icons.filled.Search
import com.ivianuu.essentials.material.icons.filled.ViewAgenda
import com.ivianuu.essentials.ui.material.BottomNavigationBar
import com.ivianuu.essentials.ui.material.BottomNavigationBarItem
import com.ivianuu.essentials.ui.material.BottomNavigationSwapper
import com.ivianuu.essentials.ui.material.ProvideBottomNavigationController
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.painter.VectorRenderable

val BottomNavigationRoute = Route {
    ProvideBottomNavigationController(
        items = BottomNavItem.values().toList()
    ) {
        Scaffold(
            topAppBar = { TopAppBar("Bottom navigation") },
            body = {
                BottomNavigationSwapper<BottomNavItem>(keepState = true) { item ->
                    ColoredRect(item.color)
                }
            },
            bottomBar = {
                BottomNavigationBar<BottomNavItem> { item ->
                    BottomNavigationBarItem(
                        icon = VectorRenderable(item.icon),
                        text = item.title
                    )
                }
            }
        )
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
        icon = Icons.Essentials.Settings,
        color = Color.Green
    )
}
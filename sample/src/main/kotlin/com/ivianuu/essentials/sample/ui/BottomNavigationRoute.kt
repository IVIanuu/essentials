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
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.BottomNavigationBar
import com.ivianuu.essentials.ui.compose.material.BottomNavigationBarItem
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade

val bottomNavigationRoute = composeControllerRoute(
    options = controllerRouteOptions().fade()
) {
    val (selectedIndex, setSelectedIndex) = +state { 0 }
    d { "compose with selected index $selectedIndex" }

    Scaffold(
        topAppBar = { EsTopAppBar("Bottom navigation") },
        body = {
            val item = BottomNavigationItem.values()[selectedIndex]
            ColoredRect(item.color)
        },
        bottomBar = {
            BottomNavigationBar(
                BottomNavigationItem.values().toList(),
                selectedIndex = selectedIndex
            ) { index, item ->
                BottomNavigationBarItem(
                    onClick = { setSelectedIndex(index) },
                    icon = { Icon(+drawableResource(item.iconRes)) },
                    title = { Text(item.title) }
                )
            }
        }
    )
}

private enum class BottomNavigationItem(
    val title: String,
    val iconRes: Int,
    val color: Color
) {
    Home(
        "Home",
        R.drawable.ic_home,
        Color.Yellow
    ),
    Mails(
        "Mails",
        R.drawable.ic_email,
        Color.Red
    ),
    Search(
        "Search",
        R.drawable.ic_search,
        Color.Blue
    ),
    Schedule(
        "Schedule",
        R.drawable.ic_view_agenda,
        Color.Cyan
    ),
    Settings(
        "Settings",
        R.drawable.ic_settings,
        Color.Green
    )
}
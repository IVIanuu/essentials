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
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.material.BottomNavigationBar
import com.ivianuu.essentials.ui.material.BottomNavigationBarItem
import com.ivianuu.essentials.ui.material.BottomNavigationSwapper
import com.ivianuu.essentials.ui.material.ProvideBottomNavigationController
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.resources.drawableResource

val BottomNavigationRoute = Route {
    try {
        error("lol")
    } catch (e: Exception) {
        e.printStackTrace()
    }

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
                        icon = drawableResource(item.iconRes),
                        text = item.title
                    )
                }
            }
        )
    }
}

private enum class BottomNavItem(
    val title: String,
    val iconRes: Int,
    val color: Color
) {
    Home(
        "Home",
        R.drawable.es_ic_home,
        Color.Yellow
    ),
    Mails(
        "Mails",
        R.drawable.ic_email,
        Color.Red
    ),
    Search(
        "Search",
        R.drawable.es_ic_search,
        Color.Blue
    ),
    Schedule(
        "Schedule",
        R.drawable.es_ic_view_agenda,
        Color.Cyan
    ),
    Settings(
        "Settings",
        R.drawable.es_ic_settings,
        Color.Green
    )
}
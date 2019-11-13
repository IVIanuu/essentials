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
import androidx.compose.Effect
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Draw
import androidx.ui.core.Opacity
import androidx.ui.core.PxSize
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.Divider
import androidx.ui.material.TopAppBar
import com.ivianuu.essentials.about.aboutRoute
import com.ivianuu.essentials.apps.ui.appPickerRoute
import com.ivianuu.essentials.apps.ui.intentAppFilter
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.staticComposable
import com.ivianuu.essentials.ui.compose.layout.FractionallySizedBox
import com.ivianuu.essentials.ui.compose.layout.OverflowBox
import com.ivianuu.essentials.ui.compose.material.PopupMenuButton
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.navigation.Route


val homeRoute = composeControllerRoute {
    FractionallySizedBox(
        widthFactor = 0.5f,
        heightFactor = 0.5f
    ) {
        OverflowBox {
            TopAppBar(title = { Text("Hello") })
        }
    }
}

@Composable
private fun HomeItem(
    item: HomeItem,
    onClick: () -> Unit
) = composable(item) {
    SimpleListItem(
        title = { Text(item.title) },
        leading = { ColorAvatar(item.color) },
        trailing = {
            PopupMenuButton(
                items = listOf(1, 2, 3),
                onSelected = {}
            ) {
                Text("Text: $it")
            }
        },
        onClick = onClick
    )
}

@Composable
private fun ColorAvatar(color: Color) = staticComposable("ColorAvatar") {
    Container(width = 40.dp, height = 40.dp) {
        val paint = +memo {
            Paint().apply { this.color = color }
        }
        Draw { canvas: Canvas, parentSize: PxSize ->
            canvas.drawCircle(
                Offset(parentSize.width.value / 2, parentSize.height.value / 2),
                parentSize.width.value / 2,
                paint
            )
        }
    }
}

@Composable
private fun HomeDivider() = staticComposable("HomeDivider") {
    Padding(left = 72.dp) {
        Opacity(0.12f) {
            Divider()
        }
    }
}

enum class HomeItem(
    val title: String,
    val color: Color,
    val route: Effect<Route>
) {
    About(
        title = "About",
        color = Color.Yellow,
        route = effectOf { aboutRoute() }
    ),
    AppPicker(
        title = "App picker",
        color = Color.Blue,
        route = effectOf {
            appPickerRoute(
                appFilter = +intentAppFilter(
                    android.content.Intent(android.provider.MediaStore.INTENT_ACTION_MUSIC_PLAYER)
                )
            )
        }
    ),
    BottomNavigation(
        title = "Bottom navigation",
        color = Color.Red,
        route = effectOf { bottomNavigationRoute }
    ),
    CheckApps(
        title = "Check apps",
        color = Color.Green,
        route = effectOf { checkAppsRoute }
    ),
    Chips(
        title = "Chips",
        color = Color.Cyan,
        route = effectOf { chipsRoute }
    ),
    Counter(
        title = "Counter",
        color = Color.Yellow,
        route = effectOf { counterRoute }
    ),
    Dialogs(
        title = "Dialogs",
        color = Color.Gray,
        route = effectOf { dialogsRoute }
    ),
    Drawer(
        title = "Drawer",
        color = Color.Blue,
        route = effectOf { drawerRoute }
    ),
    NavBar(
        title = "Nav bar",
        color = Color.Green,
        route = effectOf { navBarRoute }
    ),
    Navigation(
        title = "Navigation",
        color = Color.Red,
        route = effectOf { navigationRoute }
    ),
    Prefs(
        title = "Prefs",
        color = Color.Magenta,
        route = effectOf { prefsRoute }
    ),
    Scaffold(
        title = "Scaffold",
        color = Color.Green,
        route = effectOf { scaffoldRoute }
    ),
    Tabs(
        title = "Tabs",
        color = Color.Yellow,
        route = effectOf { tabsRoute }
    ),
    TextInput(
        title = "Text input",
        color = Color.Magenta,
        route = effectOf { textInputRoute }
    ),
    Timer(
        title = "Timer",
        color = Color.Cyan,
        route = effectOf { timerRoute }
    ),
    Twilight(
        title = "Twilight",
        color = Color.Gray,
        route = effectOf { twilightSettingsRoute }
    )
}
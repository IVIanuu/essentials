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

import android.content.Intent
import android.provider.MediaStore
import androidx.compose.Composable
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
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.about.aboutRoute
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.apps.ui.appPickerRoute
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.compose.common.navigateOnClick
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invoke
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.core.staticComposable
import com.ivianuu.essentials.ui.compose.core.staticComposableWithKey
import com.ivianuu.essentials.ui.compose.es.composeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.PopupMenuButton
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.parametersOf

val homeRoute = composeControllerRoute {
    Scaffold(
        topAppBar = {
            EsTopAppBar(
                title = { Text("Home") },
                trailing = {
                    val toaster = inject<Toaster>()
                    PopupMenuButton(
                        items = listOf(
                            "Option 1",
                            "Option 2",
                            "Option 3"
                        ),
                        onCancel = { toaster.toast("Cancelled") },
                        onSelected = { toaster.toast("Selected $it") }
                    ) {
                        Text(text = it)
                    }
                }
            )
        },
        body = {
            val items = remember { HomeItem.values().toList().sortedBy { it.name } }
            ScrollableList(
                items = items,
                itemSizeProvider = { index, _ -> if (index != items.lastIndex) 57.dp else 56.dp }
            ) { index, item ->
                staticComposableWithKey(item) {
                    Column {
                        val route = item.route()
                        HomeItem(item = item, onClick = navigateOnClick { route })
                        if (index != items.lastIndex) {
                            HomeDivider()
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun HomeItem(
    item: HomeItem,
    onClick: () -> Unit
) = composable {
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
private fun ColorAvatar(color: Color) = staticComposable {
    Container(width = 40.dp, height = 40.dp) {
        val paint = remember {
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
private fun HomeDivider() = staticComposable {
    Padding(left = 72.dp) {
        Opacity(0.12f) {
            Divider(color = (MaterialTheme.colors()().onSurface))
        }
    }
}

enum class HomeItem(
    val title: String,
    val color: Color,
    val route: @Composable() () -> Route
) {
    About(
        title = "About",
        color = Color.Yellow,
        route = { aboutRoute() }
    ),
    AppPicker(
        title = "App picker",
        color = Color.Blue,
        route = {
            appPickerRoute(
                appFilter = inject<IntentAppFilter> {
                    parametersOf(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER))
                }
            )
        }
    ),
    BottomNavigation(
        title = "Bottom navigation",
        color = Color.Red,
        route = { bottomNavigationRoute }
    ),
    CheckApps(
        title = "Check apps",
        color = Color.Green,
        route = { checkAppsRoute }
    ),
    Chips(
        title = "Chips",
        color = Color.Cyan,
        route = { chipsRoute }
    ),
    Counter(
        title = "Counter",
        color = Color.Yellow,
        route = { counterRoute }
    ),
    Dialogs(
        title = "Dialogs",
        color = Color.Gray,
        route = { dialogsRoute }
    ),
    Drawer(
        title = "Drawer",
        color = Color.Blue,
        route = { drawerRoute }
    ),
    NavBar(
        title = "Nav bar",
        color = Color.Green,
        route = { navBarRoute }
    ),
    Navigation(
        title = "Navigation",
        color = Color.Red,
        route = { navigationRoute }
    ),
    Permission(
        title = "Permission",
        color = Color.Magenta,
        route = { permissionRoute }
    ),
    Prefs(
        title = "Prefs",
        color = Color.Magenta,
        route = { prefsRoute }
    ),
    Scaffold(
        title = "Scaffold",
        color = Color.Green,
        route = { scaffoldRoute }
    ),
    Tabs(
        title = "Tabs",
        color = Color.Yellow,
        route = { tabsRoute }
    ),
    TextInput(
        title = "Text input",
        color = Color.Magenta,
        route = { textInputRoute }
    ),
    Timer(
        title = "Timer",
        color = Color.Cyan,
        route = { timerRoute }
    ),
    Twilight(
        title = "Twilight",
        color = Color.Gray,
        route = { twilightSettingsRoute }
    )
}
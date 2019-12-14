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
import androidx.compose.remember
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
import com.ivianuu.essentials.about.AboutRoute
import com.ivianuu.essentials.apps.ui.AppPickerRoute
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerRoute
import com.ivianuu.essentials.twilight.TwilightSettingsRoute
import com.ivianuu.essentials.ui.compose.common.navigateOnClick
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.layout.Column
import com.ivianuu.essentials.ui.compose.layout.ScrollableList
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.PopupMenuButton
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.parametersOf

val HomeRoute = ComposeControllerRoute {
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
                items = items
            ) { index, item ->
                Column {
                    val route = item.route()
                    HomeItem(item = item, onClick = navigateOnClick { route })
                    if (index != items.lastIndex) {
                        HomeDivider()
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
) {
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
private fun ColorAvatar(color: Color) {
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
private fun HomeDivider() {
    Padding(left = 72.dp) {
        Opacity(0.12f) {
            Divider(color = (MaterialTheme.colors().onSurface))
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
        route = { AboutRoute() }
    ),
    AppPicker(
        title = "App picker",
        color = Color.Blue,
        route = {
            AppPickerRoute(
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
        route = { CheckAppsRoute }
    ),
    Chips(
        title = "Chips",
        color = Color.Cyan,
        route = { ChipsRoute }
    ),
    Counter(
        title = "Counter",
        color = Color.Yellow,
        route = { CounterRoute }
    ),
    Dialogs(
        title = "Dialogs",
        color = Color.Gray,
        route = { DialogsRoute }
    ),
    Drawer(
        title = "Drawer",
        color = Color.Blue,
        route = { DrawerRoute }
    ),
    NavBar(
        title = "Nav bar",
        color = Color.Green,
        route = { NavBarRoute }
    ),
    Navigation(
        title = "Navigation",
        color = Color.Red,
        route = { navigationRoute }
    ),
    Permission(
        title = "Permission",
        color = Color.Magenta,
        route = { PermissionRoute }
    ),
    Prefs(
        title = "Prefs",
        color = Color.Magenta,
        route = { PrefsRoute }
    ),
    Scaffold(
        title = "Scaffold",
        color = Color.Green,
        route = { ScaffoldRoute }
    ),
    ShortcutPicker(
        title = "Shortcut picker",
        color = Color.Green,
        route = { ShortcutPickerRoute() }
    ),
    Tabs(
        title = "Tabs",
        color = Color.Yellow,
        route = { TabsRoute }
    ),
    TextInput(
        title = "Text input",
        color = Color.Magenta,
        route = { TextInputRoute }
    ),
    Timer(
        title = "Timer",
        color = Color.Cyan,
        route = { TimerRoute }
    ),
    Torch(
        title = "Torch",
        color = Color.Blue,
        route = { TorchRoute }
    ),
    Twilight(
        title = "Twilight",
        color = Color.Gray,
        route = { TwilightSettingsRoute }
    )
}
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
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Container
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.PxSize
import androidx.ui.unit.dp
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.about.AboutRoute
import com.ivianuu.essentials.apps.ui.AppPickerRoute
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerRoute
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.boolean
import com.ivianuu.essentials.twilight.TwilightSettingsRoute
import com.ivianuu.essentials.ui.box.unfoldBox
import com.ivianuu.essentials.ui.common.navigateOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.ScrollableList
import com.ivianuu.essentials.ui.material.Banner
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Icon
import com.ivianuu.essentials.ui.material.IconButton
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.PopupMenuButton
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.DefaultRouteTransition
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.UrlRoute
import com.ivianuu.essentials.ui.resources.drawableResource
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.parametersOf

val HomeRoute = Route(transition = DefaultRouteTransition) {
    d { "home route" }
    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    val toaster = inject<Toaster>()

                    IconButton(
                        image = drawableResource(R.drawable.es_ic_link),
                        onClick = {
                            d { "Clicked" }
                        }
                    )

                    PopupMenuButton(
                        items = listOf(
                            "Option 1",
                            "Option 2",
                            "Option 3"
                        ),
                        item = { Text(it) },
                        onCancel = { toaster.toast("Cancelled") },
                        onSelected = { toaster.toast("Selected $it") }
                    )
                }
            )
        },
        body = {
            ScrollableList {
                val showBanner = unfoldBox(inject<PrefBoxFactory>().boolean("show_banner")) // todo ir by
                if (showBanner.value) {
                    Banner(
                        leading = { Icon(drawableResource(R.mipmap.ic_launcher)) },
                        content = { Text("Welcome to Essentials Sample we great new features for you. Go and check them out.") },
                        actions = {
                            Button(
                                text = "Dismiss",
                                onClick = { showBanner.value = false }
                            )

                            Button(
                                text = "Learn More",
                                onClick = navigateOnClick {
                                    showBanner.value = false
                                    UrlRoute("https://google.com")
                                }
                            )
                        }
                    )
                }

                val items = remember { HomeItem.values().toList().sortedBy { it.name } }
                items.forEachIndexed { index, item ->
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
    ListItem(
        title = { Text(item.title) },
        leading = {
            val color = retain(item) {
                ColorPickerPalette.values()
                    .filter { it != ColorPickerPalette.Black && it != ColorPickerPalette.White }
                    .shuffled()
                    .first()
                    .front
            }
            ColorAvatar(color)
        },
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
    Divider(
        color = (MaterialTheme.colors().onSurface.copy(alpha = 0.12f)),
        indent = 72.dp
    )
}

enum class HomeItem(
    val title: String,
    val route: @Composable() () -> Route
) {
    About(
        title = "About",
        route = { AboutRoute() }
    ),
    Actions(
        title = "Actions",
        route = { ActionsRoute }
    ),
    AppPicker(
        title = "App picker",
        route = {
            AppPickerRoute(
                appFilter = inject<IntentAppFilter> {
                    parametersOf(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER))
                }
            )
        }
    ),
    Billing(
        title = "Billing",
        route = { BillingRoute }
    ),
    BottomNavigation(
        title = "Bottom navigation",
        route = { BottomNavigationRoute }
    ),
    CheckApps(
        title = "Check apps",
        route = { CheckAppsRoute }
    ),
    Chips(
        title = "Chips",
        route = { ChipsRoute }
    ),
    Counter(
        title = "Counter",
        route = { CounterRoute }
    ),
    Dialogs(
        title = "Dialogs",
        route = { DialogsRoute }
    ),
    Drawer(
        title = "Drawer",
        route = { DrawerRoute }
    ),
    NavBar(
        title = "Nav bar",
        route = { NavBarRoute }
    ),
    Permission(
        title = "Permission",
        route = { PermissionRoute }
    ),
    Prefs(
        title = "Prefs",
        route = { PrefsRoute }
    ),
    RestartProcess(
        title = "Restart process",
        route = { RestartProcessRoute }
    ),
    Scaffold(
        title = "Scaffold",
        route = { ScaffoldRoute }
    ),
    ShortcutPicker(
        title = "Shortcut picker",
        route = { ShortcutPickerRoute() }
    ),
    Tabs(
        title = "Tabs",
        route = { TabsRoute }
    ),
    TextInput(
        title = "Text input",
        route = { TextInputRoute }
    ),
    Timer(
        title = "Timer",
        route = { TimerRoute }
    ),
    Torch(
        title = "Torch",
        route = { TorchRoute }
    ),
    Twilight(
        title = "Twilight",
        route = { TwilightSettingsRoute }
    ),
    Unlock(
        title = "Unlock",
        route = { UnlockRoute }
    ),
    Work(
        title = "Work",
        route = { WorkRoute }
    )
}
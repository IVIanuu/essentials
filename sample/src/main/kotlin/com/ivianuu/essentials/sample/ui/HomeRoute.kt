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
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.LayoutPadding
import androidx.ui.unit.Size
import androidx.ui.unit.dp
import com.ivianuu.essentials.about.AboutRoute
import com.ivianuu.essentials.apps.ui.AppPickerRoute
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerRoute
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.twilight.TwilightSettingsRoute
import com.ivianuu.essentials.ui.box.unfoldBox
import com.ivianuu.essentials.ui.common.ColorShape
import com.ivianuu.essentials.ui.common.ScrollableList
import com.ivianuu.essentials.ui.common.navigateOnClick
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.material.Banner
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Divider
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.DefaultRouteTransition
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.UrlRoute
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.parametersOf

val HomeRoute = Route(transition = DefaultRouteTransition) {
    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    val toaster = inject<Toaster>()

                    PopupMenuButton(
                        items = listOf(
                            "Option 1",
                            "Option 2",
                            "Option 3"
                        ).map {
                            PopupMenu.Item(
                                title = it,
                                onSelected = { toaster.toast("Selected $it") }
                            )
                        }
                    )
                }
            )
        },
        body = {
            Column {
                var showBanner by unfoldBox(inject<PrefBoxFactory>().create("show_banner", false))
                if (showBanner) {
                    Banner(
                        leading = { Icon(R.mipmap.ic_launcher) },
                        content = { Text("Welcome to Essentials Sample we have great new features for you. Go and check them out.") },
                        actions = {
                            Button(onClick = { showBanner = false }) {
                                Text("Dismiss")
                            }

                            Button(
                                onClick = navigateOnClick {
                                    showBanner = false
                                    UrlRoute("https://google.com")
                                }
                            ) {
                                Text("Learn More")
                            }
                        }
                    )
                }

                val items = remember { HomeItem.values().toList().sortedBy { it.name } }

                ScrollableList(items = items) { _, item ->
                    val route = item.route()
                    HomeItem(item = item, onClick = navigateOnClick { route })
                    if (items.indexOf(item) != items.lastIndex) {
                        Divider(
                            axis = Axis.Horizontal,
                            modifier = LayoutPadding(start = 72.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun HomeItem(
    onClick: () -> Unit,
    item: HomeItem
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
            ColorShape(
                color = color,
                shape = CircleShape,
                size = Size(40.dp, 40.dp)
            )
        },
        trailing = {
            PopupMenuButton(
                items = listOf(1, 2, 3)
                    .map {
                        PopupMenu.Item(
                            onSelected = {},
                            title = it.toString()
                        )
                    }
            )
        },
        onClick = onClick
    )
}

enum class HomeItem(
    val title: String,
    val route: @Composable () -> Route
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
                appFilter = inject<IntentAppFilter>(parameters = parametersOf(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)))
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

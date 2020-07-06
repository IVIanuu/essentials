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
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.savedinstancestate.rememberSavedInstanceState
import androidx.ui.unit.dp
import com.ivianuu.essentials.about.AboutPage
import com.ivianuu.essentials.apps.ui.AppPickerPage
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerPage
import com.ivianuu.essentials.twilight.TwilightSettingsPage
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElement
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElementStackTransition
import com.ivianuu.essentials.ui.common.InsettingLazyColumnItems
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.HorizontalDivider
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.android.ActivityComponent

@Reader
@Composable
fun HomePage() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            "Option 1",
                            "Option 2",
                            "Option 3"
                        ).map { title ->
                            PopupMenu.Item(onSelected = { Toaster.toast("Selected $title") }) {
                                Text(title)
                            }
                        }
                    )
                }
            )
        }
    ) {
        val items = remember { HomeItem.values().toList().sortedBy { it.name } }

        InsettingLazyColumnItems(items = items) { item ->
            val color = key(item) {
                rememberSavedInstanceState(item) {
                    ColorPickerPalette.values()
                        .filter { it != ColorPickerPalette.Black && it != ColorPickerPalette.White }
                        .shuffled()
                        .first()
                        .front
                }
            }

            HomeItem(
                item = item,
                color = color,
                onClick = {
                    val route = when (item) {
                        HomeItem.About -> Route { AboutPage() }
                        HomeItem.Actions -> Route { ActionsPage() }
                        HomeItem.AppPicker -> Route { AppPickerPage() }
                        HomeItem.Billing -> Route { BillingPage() }
                        HomeItem.BottomNavigation -> Route { BottomNavigationPage() }
                        HomeItem.CheckApps -> Route { CheckAppsPage() }
                        HomeItem.Chips -> Route { ChipsPage() }
                        HomeItem.Counter -> Route { CounterPage() }
                        HomeItem.Dialogs -> Route { DialogsPage() }
                        HomeItem.Drawer -> Route { DrawerPage() }
                        HomeItem.DynamicSystemBars -> Route { DynamicSystemBarsPage() }
                        HomeItem.ForegroundJob -> Route { ForegroundJobPage() }
                        HomeItem.NavBar -> Route { NavBarPage() }
                        HomeItem.Notifications -> Route { NotificationsPage() }
                        HomeItem.Permission -> Route { PermissionsPage() }
                        HomeItem.Prefs -> Route { PrefsPage() }
                        HomeItem.RestartProcess -> Route { RestartProcessPage() }
                        HomeItem.Scaffold -> Route { ScaffoldPage() }
                        HomeItem.SharedElement -> Route(
                            enterTransition = SharedElementStackTransition(item to "b"),
                            exitTransition = SharedElementStackTransition(item to "b")
                        ) {
                            SharedElementPage(color)
                        }
                        HomeItem.ShortcutPicker -> Route { ShortcutPickerPage() }
                        HomeItem.Tabs -> Route { TabsPage() }
                        HomeItem.TextInput -> Route { TextInputPage() }
                        HomeItem.Timer -> Route { TimerPage() }
                        HomeItem.Torch -> Route { TorchPage() }
                        HomeItem.Twilight -> Route { TwilightSettingsPage() }
                        HomeItem.Unlock -> Route { UnlockPage() }
                        HomeItem.Work -> Route { WorkPage() }
                    }

                    navigator.push(route)
                }
            )

            if (items.indexOf(item) != items.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
            }
        }
    }
}

@Composable
private fun HomeItem(
    color: Color,
    onClick: () -> Unit,
    item: HomeItem
) {
    ListItem(
        title = { Text(item.title) },
        leading = {
            SharedElement(item) {
                Box(
                    modifier = Modifier
                        .size(40.dp),
                    backgroundColor = color,
                    shape = CircleShape
                )
            }
        },
        trailing = {
            PopupMenuButton(
                items = listOf(1, 2, 3)
                    .map { index ->
                        PopupMenu.Item(onSelected = {}) {
                            Text(index.toString())
                        }
                    }
            )
        },
        onClick = onClick
    )
}

enum class HomeItem(val title: String) {
    About(title = "About"),
    Actions(title = "Actions"),
    AppPicker(title = "App picker"),
    Billing(title = "Billing"),
    BottomNavigation(title = "Bottom navigation"),
    CheckApps(title = "Check apps"),
    Chips(title = "Chips"),
    Counter(title = "Counter"),
    Dialogs(title = "Dialogs"),
    Drawer(title = "Drawer"),
    DynamicSystemBars(title = "Dynamic system bars"),
    ForegroundJob(title = "Foreground job"),
    NavBar(title = "Nav bar"),
    Notifications(title = "Notifications"),
    Permission(title = "Permission"),
    Prefs(title = "Prefs"),
    RestartProcess(title = "Restart process"),
    Scaffold(title = "Scaffold"),
    SharedElement(title = "Shared element"),
    ShortcutPicker(title = "Shortcut picker"),
    Tabs(title = "Tabs"),
    TextInput(title = "Text input"),
    Timer(title = "Timer"),
    Torch(title = "Torch"),
    Twilight(title = "Twilight"),
    Unlock(title = "Unlock"),
    Work(title = "Work")
}

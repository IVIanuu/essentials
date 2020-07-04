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
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Unscoped

@Unscoped
class HomePage(
    private val aboutPage: AboutPage,
    private val actionsPage: ActionsPage,
    private val appPickerPage: AppPickerPage,
    private val billingPage: BillingPage,
    private val bottomNavigationPage: BottomNavigationPage,
    private val checkAppsPage: CheckAppsPage,
    private val chipsPage: ChipsPage,
    private val counterPage: CounterPage,
    private val dialogsPage: DialogsPage,
    private val drawerPage: DrawerPage,
    private val dynamicSystemBarsPage: DynamicSystemBarsPage,
    private val foregroundJobPage: ForegroundJobPage,
    private val navBarPage: NavBarPage,
    private val notificationsPage: NotificationsPage,
    private val permissionsPage: PermissionsPage,
    private val prefsPage: PrefsPage,
    private val restartProcessPage: RestartProcessPage,
    private val scaffoldPage: ScaffoldPage,
    private val sharedElementPage: SharedElementPage,
    private val tabsPage: TabsPage,
    private val textInputPage: TextInputPage,
    private val timerPage: TimerPage,
    private val torchPage: TorchPage,
    private val unlockPage: UnlockPage,
    private val workPage: WorkPage,
    private val navigator: Navigator,
    private val shortcutPickerPage: ShortcutPickerPage,
    private val toaster: Toaster,
    private val twilightSettingsPage: TwilightSettingsPage
) {
    @Composable
    operator fun invoke() {
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
                                PopupMenu.Item(onSelected = { toaster.toast("Selected $title") }) {
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
                            HomeItem.About -> Route { aboutPage() }
                            HomeItem.Actions -> Route { actionsPage() }
                            HomeItem.AppPicker -> Route { appPickerPage() }
                            HomeItem.Billing -> Route { billingPage() }
                            HomeItem.BottomNavigation -> Route { bottomNavigationPage() }
                            HomeItem.CheckApps -> Route { checkAppsPage() }
                            HomeItem.Chips -> Route { chipsPage() }
                            HomeItem.Counter -> Route { counterPage() }
                            HomeItem.Dialogs -> Route { dialogsPage() }
                            HomeItem.Drawer -> Route { drawerPage() }
                            HomeItem.DynamicSystemBars -> Route { dynamicSystemBarsPage() }
                            HomeItem.ForegroundJob -> Route { foregroundJobPage() }
                            HomeItem.NavBar -> Route { navBarPage() }
                            HomeItem.Notifications -> Route { notificationsPage() }
                            HomeItem.Permission -> Route { permissionsPage() }
                            HomeItem.Prefs -> Route { prefsPage() }
                            HomeItem.RestartProcess -> Route { restartProcessPage() }
                            HomeItem.Scaffold -> Route { scaffoldPage() }
                            HomeItem.SharedElement -> Route(
                                enterTransition = SharedElementStackTransition(item to "b"),
                                exitTransition = SharedElementStackTransition(item to "b")
                            ) {
                                sharedElementPage(color)
                            }
                            HomeItem.ShortcutPicker -> Route { shortcutPickerPage() }
                            HomeItem.Tabs -> Route { tabsPage() }
                            HomeItem.TextInput -> Route { textInputPage() }
                            HomeItem.Timer -> Route { timerPage() }
                            HomeItem.Torch -> Route { torchPage() }
                            HomeItem.Twilight -> Route { twilightSettingsPage() }
                            HomeItem.Unlock -> Route { unlockPage() }
                            HomeItem.Work -> Route { workPage() }
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

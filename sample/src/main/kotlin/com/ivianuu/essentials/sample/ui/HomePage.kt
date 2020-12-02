/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.about.AboutKey
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.backup.BackupAndRestoreKey
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerKey
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.twilight.ui.TwilightSettingsKey
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElement
import com.ivianuu.essentials.ui.animatedstack.animation.SharedElementStackTransition
import com.ivianuu.essentials.ui.common.InsettingLazyColumnFor
import com.ivianuu.essentials.ui.dialog.ColorPickerPalette
import com.ivianuu.essentials.ui.material.HorizontalDivider
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.HomeKeyBinding
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.FunBinding

@HomeKeyBinding
class HomeKey

@KeyUiBinding<HomeKey>
@FunBinding
@Composable
fun HomePage(
    dispatchNavigationAction: DispatchAction<NavigationAction>,
    showToast: showToast,
) {
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
                            PopupMenu.Item(onSelected = { showToast("Selected $title") }) {
                                Text(title)
                            }
                        }
                    )
                }
            )
        }
    ) {
        val items = remember { HomeItem.values().toList().sortedBy { it.name } }

        InsettingLazyColumnFor(items = items) { item ->
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
                    val key: Key = when (item) {
                        HomeItem.About -> AboutKey()
                        HomeItem.Actions -> ActionsKey()
                        HomeItem.AppPicker -> AppPickerKey()
                        HomeItem.AppTracker -> AppTrackerKey()
                        HomeItem.BackupRestore -> BackupAndRestoreKey()
                        HomeItem.Billing -> BillingKey()
                        HomeItem.BottomNavigation -> BottomNavigationKey()
                        HomeItem.CheckApps -> CheckAppsKey()
                        HomeItem.Chips -> ChipsKey()
                        HomeItem.Counter -> CounterKey()
                        HomeItem.Dialogs -> DialogsKey()
                        HomeItem.DisplayRotation -> DisplayRotationKey()
                        HomeItem.Drawer -> DrawerKey()
                        HomeItem.DynamicSystemBars -> DynamicSystemBarsKey()
                        HomeItem.ForegroundJob -> ForegroundJobKey()
                        HomeItem.NavBar -> NavBarKey()
                        HomeItem.Notifications -> NotificationsKey()
                        HomeItem.Permissions -> PermissionsKey()
                        HomeItem.Prefs -> PrefsKey()
                        HomeItem.RestartProcess -> RestartProcessKey()
                        HomeItem.Scaffold -> ScaffoldKey()
                        HomeItem.SharedElement -> SharedElementKey(item, color)
                        HomeItem.ShortcutPicker -> ShortcutPickerKey()
                        HomeItem.Tabs -> TabsKey()
                        HomeItem.TextInput -> TextInputKey()
                        HomeItem.Timer -> TimerKey()
                        HomeItem.Torch -> TorchKey()
                        HomeItem.Twilight -> TwilightSettingsKey()
                        HomeItem.Unlock -> UnlockKey()
                        HomeItem.Work -> WorkKey()
                    }

                    dispatchNavigationAction(NavigationAction.Push(key))
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
    item: HomeItem,
) {
    println()
    ListItem(
        title = { Text(item.title) },
        leading = {
            SharedElement(item) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, CircleShape)
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
    AppTracker(title = "App tracker"),
    BackupRestore(title = "Backup/Restore"),
    Billing(title = "Billing"),
    BottomNavigation(title = "Bottom navigation"),
    CheckApps(title = "Check apps"),
    Chips(title = "Chips"),
    Counter(title = "Counter"),
    Dialogs(title = "Dialogs"),
    DisplayRotation(title = "Display rotation"),
    Drawer(title = "Drawer"),
    DynamicSystemBars(title = "Dynamic system bars"),
    ForegroundJob(title = "Foreground job"),
    NavBar(title = "Nav bar"),
    Notifications(title = "Notifications"),
    Permissions(title = "Permission"),
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

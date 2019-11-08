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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.about.aboutRoute
import com.ivianuu.essentials.apps.ui.appPickerRoute
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.compose.common.ListScreen
import com.ivianuu.essentials.ui.compose.common.navigateOnClick
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.PopupMenuButton
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.material.currentIconStyle
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster

val homeRoute = composeControllerRoute {
    ListScreen(
        topAppBar = {
            EsTopAppBar(
                title = { Text("Home") },
                trailing = {
                    val toaster = +inject<Toaster>()

                    val iconStyle = +currentIconStyle()
                    d { "app bar icon style $iconStyle" }

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
        listBody = {
            HomeItem.values().forEachIndexed { index, item ->
                HomeItem(item = item, onClick = +navigateOnClick(item.route))
                if (index != HomeItem.values().lastIndex) {
                    HomeDivider()
                }
            }
        }
    )
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
private fun ColorAvatar(color: Color) = composable("ColorAvatar") {
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
private fun HomeDivider() = composable("HomeDivider") {
    Padding(left = 72.dp) {
        Opacity(0.12f) {
            Divider()
        }
    }
}

enum class HomeItem(
    val title: String,
    val color: Color,
    val route: () -> Route
) {
    Counter(
        title = "Counter",
        color = Color.Yellow,
        route = { counterRoute }
    ),
    Navigation(
        title = "Navigation",
        color = Color.Red,
        route = { navigationRoute }
    ),
    AppPicker(
        title = "App picker",
        color = Color.Blue,
        route = { appPickerRoute(true) }
    ),
    CheckApps(
        title = "Check apps",
        color = Color.Green,
        route = { checkAppsRoute }
    ),
    Twilight(
        title = "Twilight",
        color = Color.Gray,
        route = { twilightSettingsRoute }
    ),
    Compose(
        title = "Prefs",
        color = Color.Magenta,
        route = { prefsRoute }
    ),
    Timer(
        title = "Timer",
        color = Color.Cyan,
        route = { timerRoute }
    ),
    SecureSettings(
        title = "Nav bar",
        color = Color.Green,
        route = { navBarRoute }
    ),
    BottomNavigation(
        title = "Bottom navigation",
        color = Color.Red,
        route = { bottomNavigationRoute }
    ),
    Drawer(
        title = "Drawer",
        color = Color.Blue,
        route = { drawerRoute }
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
    Dialogs(
        title = "Dialogs",
        color = Color.Gray,
        route = { dialogsRoute }
    ),
    About(
        title = "About",
        color = Color.Yellow,
        route = { aboutRoute() }
    )
}
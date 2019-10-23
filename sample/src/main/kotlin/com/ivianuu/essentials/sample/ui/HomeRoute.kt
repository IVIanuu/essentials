package com.ivianuu.essentials.sample.ui

import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Opacity
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.Button
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.about.aboutRoute
import com.ivianuu.essentials.apps.ui.appPickerRoute
import com.ivianuu.essentials.twilight.twilightSettingsRoute
import com.ivianuu.essentials.ui.compose.common.ListScreen
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Slider
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route

val homeRoute = composeControllerRoute {
    ListScreen(
        appBar = { EsTopAppBar("Home") },
        listContent = {
            Container(
                height = 300.dp,
                expanded = true
            ) {
                Padding(16.dp) {
                    val sliderValue = +state { 60 }
                    val sliderEnabled = +state { true }
                    Column {
                        Text("Current value: ${sliderValue.value}")

                        Slider(
                            value = sliderValue.value,
                            divisions = 5,
                            onChanged = if (sliderEnabled.value) {
                                { value: Int ->
                                    d { "on changed $value" }
                                    sliderValue.value = value
                                }
                            } else {
                                null
                            },
                            onChangeStart = {
                                d { "on change start $it" }
                            },
                            onChangeEnd = {
                                d { "on change end $it" }
                            }
                        )

                        Button(
                            text = if (sliderEnabled.value) "Disable" else "Enable",
                            onClick = {
                                sliderEnabled.value = !sliderEnabled.value
                            }
                        )
                    }
                }
            }


            val navigator = +inject<Navigator>()
            HomeItem.values().forEachIndexed { index, item ->
                HomeItem(item = item, onClick = {
                    navigator.push(item.route())
                })

                if (index != HomeItem.values().lastIndex) {
                    Opacity(0.12f) {
                        Divider()
                    }
                }
            }
        }
    )
}

private fun HomeItem(
    item: HomeItem,
    onClick: () -> Unit
) = composable(item) {
    ListItem(
        text = item.title,
        onClick = onClick
    )
}

enum class HomeItem(
    val title: String,
    val route: () -> Route
) {
    Counter(
        title = "Counter",
        route = { counterRoute }
    ),
    AppPicker(
        title = "App Picker",
        route = { appPickerRoute(true) }
    ),
    CheckApps(
        title = "Check Apps",
        route = { checkAppsRoute }
    ),
    Twilight(
        title = "Twilight",
        route = { twilightSettingsRoute }
    ),
    Compose(
        title = "Compose",
        route = { composeRoute }
    ),
    About(
        title = "About",
        route = { aboutRoute() }
    )
}
package com.ivianuu.essentials.sample.ui

import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.key
import com.ivianuu.essentials.apps.ui.AppPickerRoute
import com.ivianuu.essentials.twilight.TwilightSettingsRoute
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.ListItem
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.Text

fun HomeRoute() = Route {
    Scaffold(
        appBar = { AppBar(title = "Home") },
        content = {
            RecyclerView {
                val navigator = ambient(NavigatorAmbient)
                HomeItem.values().forEach { item ->
                    HomeItem(item = item, onClick = {
                        navigator.push(item.route())
                    })
                }
            }
        }
    )
}

private fun ComponentComposition.HomeItem(
    item: HomeItem,
    onClick: () -> Unit
) {
    key(key = item) {
        ListItem(
            title = { Text(text = item.title) },
            onClick = onClick
        )
    }
}

private enum class HomeItem(
    val title: String,
    val route: () -> Route
) {
    AppBlacklist(
        title = "App Blacklist",
        route = { AppBlacklistRoute() }
    ),
    AppPicker(
        title = "App Picker",
        route = { AppPickerRoute() }
    ),
    Counter(
        title = "Counter",
        route = { CounterRoute() }
    ),
    Twilight(
        title = "Twilight",
        route = { TwilightSettingsRoute() }
    ),
    Permission(
        title = "Permission",
        route = { PermissionRoute() }
    )
}
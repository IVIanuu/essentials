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
            RecyclerView(items = HomeItem.values()) { _, item ->
                HomeItem(item = item)
            }
        }
    )
}

private fun ComponentComposition.HomeItem(item: HomeItem) {
    val navigator = ambient(NavigatorAmbient)
    key(key = item) {
        ListItem(
            title = { Text(text = item.title) },
            onClick = { navigator.push(item.route()) }
        )
    }
}

private enum class HomeItem(
    val title: String,
    val route: () -> Route
) {
    AppBlacklist(
        title = "App blacklist",
        route = { AppBlacklistRoute() }
    ),
    AppPicker(
        title = "App picker",
        route = { AppPickerRoute().withHandlers(com.ivianuu.compose.common.changehandler.FadeChangeHandler()) }
    ),
    Counter(
        title = "Counter",
        route = { CounterRoute() }
    ),
    NavBar(
        title = "Nav bar",
        route = { NavBarRoute() }
    ),
    Prefs(
        title = "Prefs",
        route = { PrefsRoute() }
    ),
    Twilight(
        title = "Twilight",
        route = { TwilightSettingsRoute().withHandlers(com.ivianuu.compose.common.changehandler.HorizontalChangeHandler()) }
    ),
    Permission(
        title = "Permission",
        route = { PermissionRoute() }
    )
}
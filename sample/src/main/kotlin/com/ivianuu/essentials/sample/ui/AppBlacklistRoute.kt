package com.ivianuu.essentials.sample.ui

import com.ivianuu.compose.common.Route
import com.ivianuu.compose.memo
import com.ivianuu.essentials.apps.ui.CheckableApps
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.coroutines.asFlow
import com.ivianuu.kprefs.stringSet

fun AppBlacklistRoute() = Route {
    val prefs = inject<KPrefs>()
    val checkedAppsPref = memo { prefs.stringSet("apps") }

    CheckableApps(
        title = "App Blacklist",
        launchableOnly = true,
        checkedAppsFlow = checkedAppsPref.asFlow(),
        onCheckedAppsChanged = { checkedAppsPref.set(it) }
    )
}
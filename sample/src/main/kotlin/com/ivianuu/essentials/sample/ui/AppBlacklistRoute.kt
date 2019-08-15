package com.ivianuu.essentials.sample.ui

import com.github.ajalt.timberkt.d
import com.ivianuu.compose.ChangeHandlers
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.changehandler.HorizontalChangeHandler
import com.ivianuu.compose.memo
import com.ivianuu.essentials.apps.ui.CheckableApps
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.coroutines.asFlow
import com.ivianuu.kprefs.stringSet
import kotlin.system.measureTimeMillis

fun AppBlacklistRoute() = Route {
    measureTimeMillis {
        val prefs = inject<KPrefs>()
        val checkedAppsPref = memo { prefs.stringSet("apps") }

        ChangeHandlers(handler = memo { HorizontalChangeHandler() }) {
            CheckableApps(
                title = "App Blacklist",
                launchableOnly = true,
                checkedAppsFlow = checkedAppsPref.asFlow(),
                onCheckedAppsChanged = { checkedAppsPref.set(it) }
            )
        }
    }.let { d { "composing app blacklist took $it ms" } }
}
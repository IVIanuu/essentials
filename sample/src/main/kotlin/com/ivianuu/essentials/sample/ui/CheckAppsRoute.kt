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

import com.ivianuu.essentials.apps.ui.CheckableAppsScreen
import com.ivianuu.essentials.apps.ui.launchableOnlyAppFilter
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.coroutines.asFlow
import com.ivianuu.kprefs.stringSet

val checkAppsRoute = composeControllerRoute(
    options = controllerRouteOptions().fade()
) {
    val prefs = inject<KPrefs>()
    val pref = memo { prefs.stringSet("apps") }
    CheckableAppsScreen(
        checkedAppsFlow = pref.asFlow(),
        onCheckedAppsChanged = pref::set,
        appBarTitle = "Send check apps",
        appFilter = launchableOnlyAppFilter()
    )
}
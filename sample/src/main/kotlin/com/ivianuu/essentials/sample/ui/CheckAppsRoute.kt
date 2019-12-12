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
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.stringSet
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.es.ComposeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.director.ControllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import kotlinx.coroutines.launch

val CheckAppsRoute = ComposeControllerRoute(
    options = ControllerRouteOptions().fade()
) {
    val boxFactory = inject<PrefBoxFactory>()
    val box = remember { boxFactory.stringSet("apps") }
    val coroutineScope = coroutineScope()
    CheckableAppsScreen(
        checkedAppsFlow = box.asFlow(),
        onCheckedAppsChanged = { newValue ->
            coroutineScope.launch {
                box.set(newValue)
            }
        },
        appBarTitle = "Send check apps",
        appFilter = inject<LaunchableAppFilter>()
    )
}
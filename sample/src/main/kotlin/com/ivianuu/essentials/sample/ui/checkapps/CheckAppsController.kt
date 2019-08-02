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

package com.ivianuu.essentials.sample.ui.checkapps

import com.ivianuu.essentials.apps.ui.CheckableAppsController
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.injekt.Inject
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.coroutines.asFlow
import com.ivianuu.kprefs.stringSet
import kotlinx.coroutines.flow.Flow

val checkAppsRoute = controllerRoute<CheckAppsController>(
    options = controllerRouteOptions().fade()
)

@Inject
class CheckAppsController(private val prefs: KPrefs) : CheckableAppsController() {

    override val toolbarTitle: String?
        get() = "Send check apps"

    private val pref by lazy { prefs.stringSet("apps") }

    override fun getCheckedAppsFlow(): Flow<Set<String>> =
        pref.asFlow()

    override fun onCheckedAppsChanged(apps: Set<String>) {
        pref.set(apps)
    }

}
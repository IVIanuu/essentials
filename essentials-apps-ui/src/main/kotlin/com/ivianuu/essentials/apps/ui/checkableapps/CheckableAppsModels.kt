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

package com.ivianuu.essentials.apps.ui.checkableapps

import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.ui.AppFilter
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.map
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow

data class CheckableAppsParams(
    val checkedApps: Flow<Set<String>>,
    val onCheckedAppsChanged: (Set<String>) -> Unit,
    val appFilter: AppFilter,
    val appBarTitle: String
)

internal typealias CheckedApps = Set<String>

@Given
fun checkedAppsSource(@Given params: CheckableAppsParams): Flow<CheckedApps> = params.checkedApps

internal typealias OnCheckedAppsChanged = (Set<String>) -> Unit

@Given
fun onCheckedAppsChanged(@Given params: CheckableAppsParams): OnCheckedAppsChanged =
    params.onCheckedAppsChanged

data class CheckableAppsState(
    val allApps: Resource<List<AppInfo>> = Idle,
    val checkedApps: Set<String> = emptySet(),
    val appFilter: AppFilter = DefaultAppFilter,
    val appBarTitle: String
) {
    val checkableApps = allApps
        .map { it.filter(appFilter) }
        .map { apps ->
            apps.map { app ->
                CheckableApp(
                    info = app,
                    isChecked = app.packageName in checkedApps
                )
            }
        }
}

@Given
fun initialCheckableAppsState(@Given params: CheckableAppsParams): @Initial CheckableAppsState = CheckableAppsState(
    appFilter = params.appFilter,
    appBarTitle = params.appBarTitle
)

data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

sealed class CheckableAppsAction {
    object SelectAll : CheckableAppsAction()
    object DeselectAll : CheckableAppsAction()
    data class UpdateAppCheckState(val app: CheckableApp, val value: Boolean) : CheckableAppsAction()
}

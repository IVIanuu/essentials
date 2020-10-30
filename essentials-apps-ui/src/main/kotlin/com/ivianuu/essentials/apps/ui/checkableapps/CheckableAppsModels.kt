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

import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.ui.AppFilter
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.map
import com.ivianuu.essentials.ui.store.StoreAction
import com.ivianuu.essentials.ui.store.StoreState

@Immutable
data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

@Immutable
data class CheckableAppsState(
    val apps: Resource<List<AppInfo>> = Idle,
    val checkedApps: Set<String> = emptySet(),
    val onCheckedAppsChanged: ((Set<String>) -> Unit)? = null,
    val appFilter: AppFilter = DefaultAppFilter,
) : StoreState {
    val checkableApps = apps
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

sealed class CheckableAppsAction : StoreAction {
    data class UpdateRefs(
        val checkedApps: Set<String>,
        val onCheckedAppsChanged: (Set<String>) -> Unit,
        val appFilter: AppFilter
    ) : CheckableAppsAction()

    data class ToggleApp(val app: CheckableApp) : CheckableAppsAction()
    object SelectAll : CheckableAppsAction()
    object DeselectAll : CheckableAppsAction()
}

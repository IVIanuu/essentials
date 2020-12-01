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

import com.ivianuu.essentials.apps.getInstalledApps
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.DeselectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.SelectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.UpdateAppCheckState
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiStateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UiStateBinding
fun checkableAppsState(
    scope: CoroutineScope,
    initial: @Initial CheckableAppsState,
    actions: Actions<CheckableAppsAction>,
    checkedAppsSource: CheckedAppsSource,
    getInstalledApps: getInstalledApps,
    onCheckedAppsChanged: OnCheckedAppsChanged
) = scope.state(initial) {
    checkedAppsSource
        .reduce { copy(checkedApps = it) }
        .launchIn(this)
    reduceResource({ getInstalledApps() }) { copy(allApps = it) }

    suspend fun pushNewCheckedApps(reducer: Set<String>.(CheckableAppsState) -> Set<String>) {
        val newCheckedApps = currentState().checkableApps()!!
            .filter { it.isChecked }
            .mapTo(mutableSetOf()) { it.info.packageName }
            .reducer(currentState())
        onCheckedAppsChanged(newCheckedApps)
    }

    actions
        .onEach { action ->
            when (action) {
                is UpdateAppCheckState -> pushNewCheckedApps {
                    if (!action.app.isChecked) {
                        this + action.app.info.packageName
                    } else {
                        this - action.app.info.packageName
                    }
                }
                SelectAll -> pushNewCheckedApps { currentState ->
                    currentState.allApps()!!.mapTo(mutableSetOf()) { it.packageName }
                }
                DeselectAll -> pushNewCheckedApps { emptySet() }
            }
        }
        .launchIn(this)
}

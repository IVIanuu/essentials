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
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiStoreBinding
import com.ivianuu.essentials.ui.store.executeIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

@UiStoreBinding
fun CoroutineScope.CheckableAppsStore(
    checkedAppsSource: CheckedAppsSource,
    onCheckedAppsChanged: OnCheckedAppsChanged,
    getInstalledApps: getInstalledApps,
    initial: @Initial CheckableAppsState
) = store<CheckableAppsState, CheckableAppsAction>(initial) {
    checkedAppsSource.setStateIn(this) { copy(checkedApps = it) }
    state
        .map { it.appFilter }
        .distinctUntilChanged()
        .mapLatest { getInstalledApps().filter(it) }
        .executeIn(this) { copy(apps = it) }

    suspend fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
        val currentState = currentState()
        val newCheckedApps = currentState.checkableApps()!!
            .filter { it.isChecked }
            .map { it.info.packageName }
            .toMutableSet()
            .apply(reducer)
        onCheckedAppsChanged(newCheckedApps)
    }

    for (action in this) {
        @Suppress("IMPLICIT_CAST_TO_ANY")
        when (action) {
            is UpdateAppCheckState -> pushNewCheckedApps {
                if (!action.app.isChecked) {
                    it += action.app.info.packageName
                } else {
                    it -= action.app.info.packageName
                }
            }
            SelectAll -> currentState().apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps += allApps.map { it.packageName }
                }
            }
            DeselectAll -> pushNewCheckedApps { it.clear() }
        }
    }
}

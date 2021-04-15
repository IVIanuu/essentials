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

import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.CoilImage
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.GetInstalledAppsUseCase
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.DeselectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.SelectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.UpdateAppCheckedState
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

typealias CheckableAppsScreen = @Composable () -> Unit

data class CheckableAppsParams(
    val checkedApps: Flow<Set<String>>,
    val onCheckedAppsChanged: (Set<String>) -> Unit,
    val appPredicate: AppPredicate,
    val appBarTitle: String
)

@Given
fun checkableAppsScreen(@Given store: Store<CheckableAppsState, CheckableAppsAction>): CheckableAppsScreen = {
    val state by store.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.appBarTitle) },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            PopupMenu.Item(
                                onSelected = { store.send(SelectAll) }
                            ) {
                                Text(stringResource(R.string.es_select_all))
                            },
                            PopupMenu.Item(
                                onSelected = { store.send(DeselectAll) }
                            ) {
                                Text(stringResource(R.string.es_deselect_all))
                            }
                        )
                    )
                }
            )
        }
    ) {
        ResourceLazyColumnFor(state.checkableApps) { app ->
            ListItem(
                title = { Text(app.info.appName) },
                leading = {
                    CoilImage(
                        data = AppIcon(packageName = app.info.packageName),
                        modifier = Modifier.size(40.dp),
                        contentDescription = null
                    )
                },
                trailing = {
                    Checkbox(
                        checked = app.isChecked,
                        onCheckedChange = null
                    )
                },
                onClick = { store.send(UpdateAppCheckedState(app, !app.isChecked)) }
            )
        }
    }
}

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
    val appPredicate: AppPredicate = DefaultAppPredicate,
    val appBarTitle: String
) {
    val checkableApps = allApps
        .map { it.filter(appPredicate) }
        .map { apps ->
            apps.map { app ->
                CheckableApp(
                    info = app,
                    isChecked = app.packageName in checkedApps
                )
            }
        }
    companion object {
        @Given
        fun initial(@Given params: CheckableAppsParams): @Initial CheckableAppsState = CheckableAppsState(
            appPredicate = params.appPredicate,
            appBarTitle = params.appBarTitle
        )
    }
}

data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

sealed class CheckableAppsAction {
    data class UpdateAppCheckedState(
        val app: CheckableApp,
        val value: Boolean
    ) : CheckableAppsAction()
    object SelectAll : CheckableAppsAction()
    object DeselectAll : CheckableAppsAction()
}

@Given
fun checkableAppsStore(
    @Given checkedApps: Flow<CheckedApps>,
    @Given getInstalledApps: GetInstalledAppsUseCase,
    @Given onCheckedAppsChanged: OnCheckedAppsChanged,
): StoreBuilder<KeyUiGivenScope, CheckableAppsState, CheckableAppsAction> = {
    checkedApps
        .updateIn(this) { copy(checkedApps = it) }
    resourceFlow { emit(getInstalledApps()) }
        .updateIn(this) { copy(allApps = it) }
    suspend fun pushNewCheckedApps(transform: Set<String>.(CheckableAppsState) -> Set<String>) {
        val currentState = state.first()
        val newCheckedApps = currentState.checkableApps.get()
            ?.filter { it.isChecked }
            ?.mapTo(mutableSetOf()) { it.info.packageName }
            ?.transform(currentState)
            ?: return
        onCheckedAppsChanged(newCheckedApps)
    }
    action<UpdateAppCheckedState> { action ->
        pushNewCheckedApps {
            if (!action.app.isChecked) {
                this + action.app.info.packageName
            } else {
                this - action.app.info.packageName
            }
        }
    }
    action<SelectAll> {
        pushNewCheckedApps { currentState ->
            currentState.allApps.get()!!.mapTo(mutableSetOf()) { it.packageName }
        }
    }
    action<DeselectAll> {
        pushNewCheckedApps { emptySet() }
    }
}

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
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppFilter
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.DeselectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.SelectAll
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsAction.UpdateAppCheckState
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.resource.map
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

typealias CheckableAppsScreen = @Composable () -> Unit

data class CheckableAppsParams(
    val checkedApps: Flow<Set<String>>,
    val onCheckedAppsChanged: (Set<String>) -> Unit,
    val appFilter: AppFilter,
    val appBarTitle: String
)

@Given
fun checkableAppsScreen(
    @Given stateFlow: StateFlow<CheckableAppsState>,
    @Given dispatch: Collector<CheckableAppsAction>,
): CheckableAppsScreen = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.appBarTitle) },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            PopupMenu.Item(
                                onSelected = {
                                    dispatch(SelectAll)
                                }
                            ) {
                                Text(stringResource(R.string.es_select_all))
                            },
                            PopupMenu.Item(
                                onSelected = {
                                    dispatch(DeselectAll)
                                }
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
            CheckableApp(
                app = app,
                onClick = { dispatch(UpdateAppCheckState(app, !app.isChecked)) }
            )
        }
    }
}

@Composable
private fun CheckableApp(
    onClick: () -> Unit,
    app: CheckableApp
) {
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
                onCheckedChange = { onClick() }
            )
        },
        onClick = onClick
    )
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

@Scoped<KeyUiComponent>
@Given
fun checkableAppsState(
    @Given scope: KeyUiScope,
    @Given initial: @Initial CheckableAppsState,
    @Given actions: Flow<CheckableAppsAction>,
    @Given appRepository: AppRepository,
    @Given checkedApps: Flow<CheckedApps>,
    @Given onCheckedAppsChanged: OnCheckedAppsChanged
): StateFlow<CheckableAppsState> = scope.state(initial) {
    checkedApps
        .reduce { copy(checkedApps = it) }
        .launchIn(this)
    reduceResource({ appRepository.getInstalledApps() }) { copy(allApps = it) }

    suspend fun pushNewCheckedApps(reducer: Set<String>.(CheckableAppsState) -> Set<String>) {
        val newCheckedApps = currentState().checkableApps()
            ?.filter { it.isChecked }
            ?.mapTo(mutableSetOf()) { it.info.packageName }
            ?.reducer(currentState())
            ?: return
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

@Scoped<KeyUiComponent>
@Given
val checkableAppsActions get() = EventFlow<CheckableAppsAction>()
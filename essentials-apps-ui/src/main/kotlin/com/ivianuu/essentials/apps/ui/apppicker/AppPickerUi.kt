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

package com.ivianuu.essentials.apps.ui.apppicker

import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.CoilImage
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppFilter
import com.ivianuu.essentials.apps.ui.DefaultAppFilter
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerAction.FilterApps
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerAction.PickApp
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.resource.map
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppPickerKey(
    val appFilter: AppFilter = DefaultAppFilter,
    val title: String? = null,
) : Key<AppInfo>

@Given
val appPickerKeyModule = KeyModule<AppPickerKey>()

@Given
fun appPickerUi(
    @Given stateFlow: StateFlow<AppPickerState>,
    @Given dispatch: Collector<AppPickerAction>,
): KeyUi<AppPickerKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title ?: stringResource(R.string.es_title_app_picker)) }
            )
        }
    ) {
        ResourceLazyColumnFor(state.filteredApps) { app ->
            key(app.packageName) {
                AppInfo(
                    onClick = { dispatch(PickApp(app)) },
                    app = app
                )
            }
        }
    }
}

@Composable
private fun AppInfo(
    onClick: () -> Unit,
    app: AppInfo,
) {
    ListItem(
        title = { Text(app.appName) },
        leading = {
            CoilImage(
                data = AppIcon(packageName = app.packageName),
                modifier = Modifier.size(40.dp),
                contentDescription = null
            )
        },
        onClick = onClick
    )
}

data class AppPickerState(
    private val allApps: Resource<List<AppInfo>> = Idle,
    val appFilter: AppFilter = DefaultAppFilter,
    val title: String? = null
) {
    val filteredApps = allApps
        .map { it.filter(appFilter) }
}

@Given
fun initialAppPickerState(@Given key: AppPickerKey): @Initial AppPickerState = AppPickerState(
    appFilter = key.appFilter,
    title = key.title
)

sealed class AppPickerAction {
    data class FilterApps(val appFilter: AppFilter) : AppPickerAction()
    data class PickApp(val app: AppInfo) : AppPickerAction()
}

@Given
fun appPickerState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial AppPickerState,
    @Given actions: Flow<AppPickerAction>,
    @Given appRepository: AppRepository,
    @Given key: AppPickerKey,
    @Given navigator: Collector<NavigationAction>,
): @Scoped<KeyUiGivenScope> StateFlow<AppPickerState> = scope.state(initial) {
    reduceResource({ appRepository.getInstalledApps() }) { copy(allApps = it) }
    actions
        .filterIsInstance<FilterApps>()
        .reduce { copy(appFilter = it.appFilter) }
        .launchIn(this)
    actions
        .filterIsInstance<PickApp>()
        .onEach { navigator(Pop(key, it.app)) }
        .launchIn(this)
}

@Given
val appPickerActions: @Scoped<KeyUiGivenScope> MutableSharedFlow<AppPickerAction>
    get() = EventFlow()

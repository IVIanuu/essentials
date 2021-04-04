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
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.ViewModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

class AppPickerKey(
    val appFilter: AppFilter = DefaultAppFilter,
    val title: String? = null,
) : Key<AppInfo>

@Given
val appPickerUi: ViewModelKeyUi<AppPickerKey, AppPickerViewModel, AppPickerState> = { viewModel, state ->
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(state.title ?: stringResource(R.string.es_title_app_picker))
                }
            )
        }
    ) {
        ResourceLazyColumnFor(state.filteredApps) { app ->
            ListItem(
                title = { Text(app.appName) },
                leading = {
                    CoilImage(
                        data = AppIcon(packageName = app.packageName),
                        modifier = Modifier.size(40.dp),
                        contentDescription = null
                    )
                },
                onClick = { viewModel.pickApp(app) }
            )
        }
    }
}

data class AppPickerState(
    private val allApps: Resource<List<AppInfo>> = Idle,
    val appFilter: AppFilter = DefaultAppFilter,
    val title: String? = null
) : State() {
    val filteredApps = allApps
        .map { it.filter(appFilter) }
    companion object {
        @Given
        fun initial(@Given key: AppPickerKey): @Initial AppPickerState = AppPickerState(
            appFilter = key.appFilter,
            title = key.title
        )
    }
}

@Scoped<KeyUiGivenScope>
@Given
class AppPickerViewModel(
    @Given private val appRepository: AppRepository,
    @Given private val key: AppPickerKey,
    @Given private val navigator: Navigator,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, AppPickerState>
): StateFlow<AppPickerState> by store {
    init {
        resourceFlow { emit(appRepository.getInstalledApps()) }
            .updateIn(store) { copy(allApps = it) }
    }

    fun pickApp(app: AppInfo) = store.effect {
        navigator.pop(key, app)
    }
}

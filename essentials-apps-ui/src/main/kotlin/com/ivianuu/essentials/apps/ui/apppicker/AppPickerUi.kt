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
import com.ivianuu.essentials.apps.GetInstalledAppsUseCase
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppPredicate
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.R
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerAction.PickApp
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Given

class AppPickerKey(
    val appPredicate: AppPredicate = DefaultAppPredicate,
    val title: String? = null,
) : Key<AppInfo>

@Given
val appPickerUi: StoreKeyUi<AppPickerKey, AppPickerState, AppPickerAction> = {
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
                onClick = { send(PickApp(app)) }
            )
        }
    }
}

data class AppPickerState(
    private val allApps: Resource<List<AppInfo>> = Idle,
    val appPredicate: AppPredicate = DefaultAppPredicate,
    val title: String? = null
) {
    val filteredApps = allApps
        .map { it.filter(appPredicate) }
    companion object {
        @Given
        fun initial(@Given key: AppPickerKey): @Initial AppPickerState = AppPickerState(
            appPredicate = key.appPredicate,
            title = key.title
        )
    }
}

sealed class AppPickerAction {
    data class PickApp(val app: AppInfo) : AppPickerAction()
}

@Given
fun appPickerStore(
    @Given key: AppPickerKey,
    @Given getInstalledApps: GetInstalledAppsUseCase,
    @Given navigator: Navigator,
): StoreBuilder<KeyUiGivenScope, AppPickerState, AppPickerAction> = {
    resourceFlow { emit(getInstalledApps()) }
        .updateIn(this) { copy(allApps = it) }
    action<PickApp> { navigator.pop(key, it.app) }
}

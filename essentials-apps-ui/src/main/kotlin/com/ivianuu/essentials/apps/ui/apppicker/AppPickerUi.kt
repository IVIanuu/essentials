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
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.map
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Given

class AppPickerKey(
    val appPredicate: AppPredicate = DefaultAppPredicate,
    val title: String? = null,
) : Key<AppInfo>

@Given
val appPickerUi: ModelKeyUi<AppPickerKey, AppPickerModel> = {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(model.title ?: stringResource(R.string.es_title_app_picker))
                }
            )
        }
    ) {
        ResourceLazyColumnFor(model.filteredApps) { app ->
            ListItem(
                title = { Text(app.appName) },
                leading = {
                    CoilImage(
                        data = AppIcon(packageName = app.packageName),
                        modifier = Modifier.size(40.dp),
                        contentDescription = null
                    )
                },
                onClick = { model.pickApp(app) }
            )
        }
    }
}

@Optics
data class AppPickerModel(
    private val allApps: Resource<List<AppInfo>> = Idle,
    val appPredicate: AppPredicate = DefaultAppPredicate,
    val title: String? = null,
    val pickApp: (AppInfo) -> Unit = {}
) {
    val filteredApps = allApps
        .map { it.filter(appPredicate) }
    companion object {
        @Given
        fun initial(@Given key: AppPickerKey): @Initial AppPickerModel = AppPickerModel(
            appPredicate = key.appPredicate,
            title = key.title
        )
    }
}

@Given
fun appPickerModel(
    @Given key: AppPickerKey,
    @Given getInstalledApps: GetInstalledAppsUseCase,
    @Given navigator: Navigator,
): StateBuilder<KeyUiGivenScope, AppPickerModel> = {
    resourceFlow { emit(getInstalledApps()) }
        .updateIn(this) { copy(allApps = it) }
    action(AppPickerModel.pickApp()) { navigator.pop(key, it) }
}

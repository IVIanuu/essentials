/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.apps.ui

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.size
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.getInstalledApps
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.viewmodel.StateViewModel
import com.ivianuu.essentials.ui.viewmodel.currentState
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@Reader
@Composable
fun AppPickerPage(
    appFilter: AppFilter = DefaultAppFilter,
    title: String? = null
) {
    val viewModel = viewModel(appFilter) {
        given<(AppFilter) -> AppPickerViewModel>()(appFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title ?: stringResource(R.string.es_title_app_picker)) }
            )
        }
    ) {
        ResourceLazyColumnItems(
            resource = viewModel.currentState.apps
        ) { app ->
            key(app.packageName) {
                AppInfo(
                    onClick = { viewModel.appClicked(app) },
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
                modifier = Modifier.size(40.dp)
            )
        },
        onClick = onClick
    )
}

@Given
internal class AppPickerViewModel(
    private val appFilter: AppFilter
) : StateViewModel<AppPickerState>(AppPickerState()) {

    init {
        execute(
            block = {
                getInstalledApps()
                    .filter(appFilter)
            },
            reducer = { copy(apps = it) }
        )
    }

    fun appClicked(app: AppInfo) {
        navigator.popTop(result = app)
    }
}

@Immutable
internal data class AppPickerState(val apps: Resource<List<AppInfo>> = Idle)

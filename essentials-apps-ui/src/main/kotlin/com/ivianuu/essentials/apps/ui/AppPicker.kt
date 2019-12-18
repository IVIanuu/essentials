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
import androidx.lifecycle.viewModelScope
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Center
import androidx.ui.res.stringResource
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.coil.Image
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.mvrx.injectMvRxViewModel
import com.ivianuu.essentials.ui.compose.common.AsyncList
import com.ivianuu.essentials.ui.compose.layout.SizedBox
import com.ivianuu.essentials.ui.compose.material.AvatarIconStyle
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.navigation.NavigatorState
import com.ivianuu.essentials.ui.compose.navigation.Route
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun AppPickerRoute(
    title: String? = null,
    appFilter: AppFilter = DefaultAppFilter
) = Route {
    Scaffold(
        topAppBar = { EsTopAppBar(title ?: stringResource(R.string.es_title_app_picker)) },
        body = {
            val viewModel =
                injectMvRxViewModel<AppPickerViewModel> {
                    parametersOf(appFilter)
                }

            AsyncList(
                state = viewModel.state.apps,
                successItem = { _, app ->
                    key(app.packageName) {
                        AppInfo(
                            app = app,
                            onClick = { viewModel.appClicked(app) }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun AppInfo(
    app: AppInfo,
    onClick: () -> Unit
) {
    SimpleListItem(
        title = { Text(app.appName) },
        leading = {
            SizedBox(size = 40.dp) {
                Center {
                    Image(data = AppIcon(app.packageName)) {
                        Icon(image = it, style = AvatarIconStyle())
                    }
                }
            }
        },
        onClick = onClick
    )
}

@Factory
internal class AppPickerViewModel(
    @Param private val appFilter: AppFilter,
    private val appStore: AppStore,
    dispatchers: AppDispatchers,
    private val navigator: NavigatorState
) : MvRxViewModel<AppPickerState>(AppPickerState()) {

    init {
        viewModelScope.execute(
            context = dispatchers.default,
            block = {
                appStore.getInstalledApps()
                    .filter(appFilter)
            },
            reducer = { copy(apps = it) }
        )
    }

    fun appClicked(app: AppInfo) {
        navigator.pop(result = app)
    }
}

@Immutable
internal data class AppPickerState(val apps: Async<List<AppInfo>> = Uninitialized)

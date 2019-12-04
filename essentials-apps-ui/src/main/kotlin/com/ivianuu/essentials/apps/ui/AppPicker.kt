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
import androidx.lifecycle.viewModelScope
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Center
import androidx.ui.material.CircularProgressIndicator
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.staticComposableWithKey
import com.ivianuu.essentials.ui.compose.es.composeControllerRoute
import com.ivianuu.essentials.ui.compose.image.Image
import com.ivianuu.essentials.ui.compose.material.AvatarIconStyle
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Icon
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.ui.compose.mvrx.mvRxViewModel
import com.ivianuu.essentials.ui.compose.resources.stringResource
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun appPickerRoute(
    title: String? = null,
    appFilter: AppFilter = DefaultAppFilter
) = composeControllerRoute {
    Scaffold(
        topAppBar = {
            EsTopAppBar(title ?: stringResource(R.string.es_title_app_picker))
        },
        body = {
            val viewModel = mvRxViewModel<AppPickerViewModel> {
                parametersOf(appFilter)
            }

            when (viewModel.state.apps) {
                is Loading -> {
                    composable {
                        Center {
                            CircularProgressIndicator()
                        }
                    }
                }
                is Success -> {
                    composable {
                        ScrollableList(
                            items = viewModel.state.apps() ?: emptyList(),
                            itemSize = 56.dp
                        ) { _, app ->
                            AppInfo(
                                app = app,
                                onClick = { viewModel.appClicked(app) }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AppInfo(
    app: AppInfo,
    onClick: () -> Unit
) = staticComposableWithKey(app.packageName) {
    SimpleListItem(
        title = { Text(app.appName) },
        leading = {
            Image(data = AppIcon(app.packageName)) {
                Icon(image = it, style = AvatarIconStyle())
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
    private val navigator: Navigator
) : MvRxViewModel<AppPickerState>(AppPickerState()) {

    init {
        viewModelScope.execute(
            context = dispatchers.io,
            block = {
                appStore.getInstalledApps()
                    .filter(appFilter)
            },
            reducer = { copy(apps = it) }
        )
    }

    fun appClicked(app: AppInfo) {
        navigator.pop(app)
    }
}

internal data class AppPickerState(val apps: Async<List<AppInfo>> = Uninitialized)
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
import androidx.ui.layout.LayoutSize
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.android.ui.common.RenderAsyncList
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.material.ListItem
import com.ivianuu.essentials.android.ui.material.Scaffold
import com.ivianuu.essentials.android.ui.material.TopAppBar
import com.ivianuu.essentials.android.ui.navigation.NavigatorState
import com.ivianuu.essentials.android.ui.navigation.Route
import com.ivianuu.essentials.android.util.Async
import com.ivianuu.essentials.android.util.Uninitialized
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.mvrx.injectMvRxViewModel
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

fun AppPickerRoute(
    title: String? = null,
    appFilter: AppFilter = DefaultAppFilter
) = Route {
    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text(title ?: stringResource(R.string.es_title_app_picker)) }
            )
        },
        body = {
            val viewModel =
                injectMvRxViewModel<AppPickerViewModel>(parameters = parametersOf(appFilter))

            RenderAsyncList(
                state = viewModel.getCurrentState().apps,
                successItemCallback = { _, app ->
                    key(app.packageName) {
                        AppInfo(
                            onClick = { viewModel.appClicked(app) },
                            app = app
                        )
                    }
                }
            )
        }
    )
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
                modifier = LayoutSize(40.dp)
            )
        },
        onClick = onClick
    )
}

@Factory
private class AppPickerViewModel(
    @Param private val appFilter: AppFilter,
    private val appStore: AppStore,
    private val navigator: NavigatorState
) : MvRxViewModel<AppPickerState>(AppPickerState()) {

    init {
        coroutineScope.execute(
            block = {
                appStore.getInstalledApps()
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
internal data class AppPickerState(val apps: Async<List<AppInfo>> = Uninitialized)

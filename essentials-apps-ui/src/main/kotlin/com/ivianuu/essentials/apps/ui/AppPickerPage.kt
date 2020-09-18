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

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.getInstalledApps
import com.ivianuu.essentials.apps.ui.AppPickerAction.AppClicked
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope

@Reader
@Composable
fun AppPickerPage(
    appFilter: AppFilter = DefaultAppFilter,
    title: String? = null
) {
    val (state, dispatch) = rememberStore<AppPickerState, AppPickerAction> {
        given(this, appFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title ?: stringResource(R.string.es_title_app_picker)) }
            )
        }
    ) {
        ResourceLazyColumnItems(state.apps) { app ->
            key(app.packageName) {
                AppInfo(
                    onClick = { dispatch(AppClicked(app)) },
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
internal fun CoroutineScope.appPickerStore(
    appFilter: AppFilter
) = store<AppPickerState, AppPickerAction>(AppPickerState()) {
    execute(
        block = {
            getInstalledApps()
                .filter(appFilter)
        },
        reducer = { copy(apps = it) }
    )

    onEachAction {
        when (it) {
            is AppClicked -> navigator.popTop(result = it.app)
        }.exhaustive
    }
}

@Immutable
internal data class AppPickerState(val apps: Resource<List<AppInfo>> = Idle)

internal sealed class AppPickerAction {
    data class AppClicked(val app: AppInfo) : AppPickerAction()
}


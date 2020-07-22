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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.size
import androidx.ui.material.Checkbox
import androidx.ui.unit.dp
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.getInstalledApps
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.resource.invoke
import com.ivianuu.essentials.ui.viewmodel.StateViewModel
import com.ivianuu.essentials.ui.viewmodel.currentState
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Reader
@Composable
fun CheckableAppsPage(
    checkedApps: Flow<Set<String>>,
    onCheckedAppsChanged: suspend (Set<String>) -> Unit,
    appBarTitle: String,
    appFilter: AppFilter = DefaultAppFilter
) {
    val viewModel = viewModel(
        appFilter,
        checkedApps,
        onCheckedAppsChanged
    ) {
        given<(
            AppFilter,
            Flow<Set<String>>,
            suspend (Set<String>) -> Unit
        ) -> CheckableAppsViewModel>()(appFilter, checkedApps, onCheckedAppsChanged)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appBarTitle) },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            PopupMenu.Item(onSelected = { viewModel.selectAllClicked() }) {
                                Text(R.string.es_select_all)
                            },
                            PopupMenu.Item(onSelected = { viewModel.deselectAllClicked() }) {
                                Text(R.string.es_deselect_all)
                            }
                        )
                    )
                }
            )
        }
    ) {
        ResourceLazyColumnItems(resource = viewModel.currentState.apps) { app ->
            CheckableApp(
                app = app,
                onClick = { viewModel.appClicked(app) }
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
                modifier = Modifier.size(40.dp)
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

@Given
internal class CheckableAppsViewModel(
    private val appFilter: AppFilter,
    private val checkedApps: Flow<Set<String>>,
    private val onCheckedAppsChanged: suspend (Set<String>) -> Unit
) : StateViewModel<CheckableAppsState>(CheckableAppsState()) {

    init {
        combine(
            flow {
                emit(
                    getInstalledApps()
                        .filter(appFilter)
                )
            },
            checkedApps
        ) { apps, checked ->
            apps
                .map {
                    CheckableApp(
                        it,
                        it.packageName in checked
                    )
                }
                .toList()
        }.execute { copy(apps = it) }
    }

    fun appClicked(app: CheckableApp) {
        scope.launch {
            pushNewCheckedApps {
                if (!app.isChecked) {
                    it += app.info.packageName
                } else {
                    it -= app.info.packageName
                }
            }
        }
    }

    fun selectAllClicked() {
        scope.launch {
            state.value.apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps += allApps.map { it.info.packageName }
                }
            }
        }
    }

    fun deselectAllClicked() {
        scope.launch {
            pushNewCheckedApps { it.clear() }
        }
    }

    private suspend fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
        val newCheckedApps = state.value.apps()!!
            .filter { it.isChecked }
            .map { it.info.packageName }
            .toMutableSet()
            .apply(reducer)
        onCheckedAppsChanged(newCheckedApps)
    }
}

@Immutable
internal data class CheckableAppsState(
    val apps: Resource<List<CheckableApp>> = Idle
)

@Immutable
internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

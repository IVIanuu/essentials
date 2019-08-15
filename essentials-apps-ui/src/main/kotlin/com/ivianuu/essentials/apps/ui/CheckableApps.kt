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

import androidx.lifecycle.viewModelScope
import com.ivianuu.compose.ChangeHandlers
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.common.RecyclerView
import com.ivianuu.compose.common.changehandler.FadeChangeHandler
import com.ivianuu.compose.key
import com.ivianuu.compose.memo
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.ui.compose.AppBar
import com.ivianuu.essentials.ui.compose.CheckBox
import com.ivianuu.essentials.ui.compose.ListItem
import com.ivianuu.essentials.ui.compose.Scaffold
import com.ivianuu.essentials.ui.compose.SimpleLoading
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.injectMvRxViewModel
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.essentials.util.flowOf
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun ComponentComposition.CheckableApps(
    checkedAppsFlow: Flow<Set<String>>,
    title: String? = null,
    launchableOnly: Boolean = false,
    onCheckedAppsChanged: (Set<String>) -> Unit
) {
    val (state, viewModel) = injectMvRxViewModel(CheckableAppsViewModel::class) {
        parametersOf(launchableOnly, checkedAppsFlow, onCheckedAppsChanged)
    }

    Scaffold(
        appBar = {
            AppBar(
                title = title,
                menu = PopupMenu(
                    items = listOf(
                        PopupMenuItem(
                            value = MenuOption.SelectAll,
                            titleRes = R.string.es_select_all
                        ),
                        PopupMenuItem(
                            value = MenuOption.DeselectAll,
                            titleRes = R.string.es_deselect_all
                        )
                    ),
                    onSelected = {
                        when (it) {
                            MenuOption.SelectAll -> viewModel.selectAllClicked()
                            MenuOption.DeselectAll -> viewModel.deselectAllClicked()
                        }
                    }
                )
            )
        },
        content = {
            ChangeHandlers(handler = memo { FadeChangeHandler() }) {
                when (state.apps) {
                    is Loading -> SimpleLoading()
                    is Success -> {
                        ChangeHandlers(handler = null) {
                            RecyclerView {
                                state.apps()?.forEach { app ->
                                    CheckableApp(app = app, onClick = {
                                        viewModel.appClicked(app)
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

private fun ComponentComposition.CheckableApp(
    app: CheckableApp,
    onClick: () -> Unit
) {
    key(key = app.info.packageName) {
        ListItem(
            title = app.info.appName,
            leadingAction = { AppIcon(packageName = app.info.packageName) },
            trailingAction = {
                CheckBox(
                    value = app.isChecked,
                    onChange = {}
                )
            },
            onClick = onClick
        )
    }
}

private enum class MenuOption { SelectAll, DeselectAll }

@Inject
internal class CheckableAppsViewModel(
    @Param private val launchableOnly: Boolean,
    @Param private val checkedAppsFlow: Flow<Set<String>>,
    @Param private val onCheckedAppsChanged: (Set<String>) -> Unit,
    private val appStore: AppStore,
    private val dispatchers: AppDispatchers
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    init {
        viewModelScope.launch(dispatchers.io) {
            val appsFlow = flowOf {
                if (launchableOnly) appStore.getLaunchableApps() else appStore.getInstalledApps()
            }

            appsFlow.combineLatest(checkedAppsFlow) { apps, checked ->
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
    }

    fun appClicked(app: CheckableApp) {
        viewModelScope.launch(dispatchers.io) {
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
        viewModelScope.launch(dispatchers.io) {
            state.apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps += allApps.map { it.info.packageName }
                }
            }
        }
    }

    fun deselectAllClicked() {
        viewModelScope.launch(dispatchers.io) {
            pushNewCheckedApps { it.clear() }
        }
    }

    private suspend fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
        checkedAppsFlow.first()
            .toMutableSet()
            .apply(reducer)
            .let { onCheckedAppsChanged(it) }
    }
}

internal data class CheckableAppsState(
    val apps: Async<List<CheckableApp>> = Uninitialized
)

internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)
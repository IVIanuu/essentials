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
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.lifecycle.viewModelScope
import androidx.ui.core.dp
import androidx.ui.layout.LayoutSize
import androidx.ui.res.stringResource
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.coil.Image
import com.ivianuu.essentials.coroutines.StateFlow
import com.ivianuu.essentials.coroutines.flowOf
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.mvrx.injectMvRxViewModel
import com.ivianuu.essentials.ui.common.AsyncList
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.AvatarIconStyle
import com.ivianuu.essentials.ui.material.Checkbox
import com.ivianuu.essentials.ui.material.Icon
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.PopupMenuButton
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import com.ivianuu.scopes.ReusableScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun CheckableAppsScreen(
    checkedAppsFlow: Flow<Set<String>>,
    onCheckedAppsChanged: (Set<String>) -> Unit,
    appBarTitle: String,
    appFilter: AppFilter = DefaultAppFilter
) {
    val viewModel =
        injectMvRxViewModel<CheckableAppsViewModel> {
            parametersOf(appFilter)
        }

    onCommit(checkedAppsFlow, onCheckedAppsChanged) {
        viewModel.attach(checkedAppsFlow, onCheckedAppsChanged)
        onDispose { viewModel.detach() }
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text(appBarTitle) },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            PopupMenu.Item(
                                text = stringResource(R.string.es_select_all),
                                onSelected = { viewModel.selectAllClicked() }
                            ),
                            PopupMenu.Item(
                                text = stringResource(R.string.es_deselect_all),
                                onSelected = { viewModel.deselectAllClicked() }
                            )
                        )
                    )
                }
            )
        },
        body = {
            AsyncList(
                state = viewModel.state.apps,
                successItem = { _, app ->
                    key(app.info.packageName) {
                        CheckableApp(
                            app = app,
                            onClick = remember(app) {
                                { viewModel.appClicked(app) }
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun CheckableApp(
    app: CheckableApp,
    onClick: () -> Unit
) {
    ListItem(
        title = { Text(app.info.appName) },
        leading = {
            Image(
                data = AppIcon(app.info.packageName),
                modifier = LayoutSize(width = 40.dp, height = 40.dp)
            ) {
                if (it != null) Icon(image = it, style = AvatarIconStyle())
            }
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

@Factory
internal class CheckableAppsViewModel(
    @Param private val appFilter: AppFilter,
    private val appStore: AppStore,
    private val dispatchers: AppDispatchers
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    private var onCheckedAppsChanged: ((Set<String>) -> Unit)? = null
    private val checkedAppsScope = ReusableScope()
    private val checkedApps = StateFlow<Set<String>>()

    init {
        viewModelScope.launch(dispatchers.default) {
            val appsFlow = flowOf {
                appStore.getInstalledApps().filter(appFilter)
            }

            appsFlow.combine(checkedApps) { apps, checked ->
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

    fun attach(
        checkedAppsFlow: Flow<Set<String>>,
        onCheckedAppsChanged: (Set<String>) -> Unit
    ) {
        this.onCheckedAppsChanged = onCheckedAppsChanged

        checkedAppsFlow
            .onEach { checkedApps.value = it }
            .launchIn(checkedAppsScope.coroutineScope)
    }

    fun detach() {
        checkedAppsScope.clear()
        onCheckedAppsChanged = null
    }

    fun appClicked(app: CheckableApp) {
        viewModelScope.launch(dispatchers.default) {
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
        viewModelScope.launch(dispatchers.default) {
            state.apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps += allApps.map { it.info.packageName }
                }
            }
        }
    }

    fun deselectAllClicked() {
        viewModelScope.launch(dispatchers.default) {
            pushNewCheckedApps { it.clear() }
        }
    }

    private suspend fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
        val newCheckedApps = checkedApps.value
            .toMutableSet()
            .apply(reducer)
        onCheckedAppsChanged?.invoke(newCheckedApps)
    }
}

@Immutable
internal data class CheckableAppsState(
    val apps: Async<List<CheckableApp>> = Uninitialized
)

@Immutable
internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

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
import androidx.compose.key
import androidx.compose.onActive
import androidx.lifecycle.viewModelScope
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Center
import androidx.ui.material.Checkbox
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
import com.ivianuu.essentials.ui.compose.material.PopupMenuButton
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.flowOf
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf
import com.ivianuu.scopes.ReusableScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    onActive {
        viewModel.attach(checkedAppsFlow, onCheckedAppsChanged)
        onDispose { viewModel.detach() }
    }

    Scaffold(
        topAppBar = {
            EsTopAppBar(
                title = { Text(appBarTitle) },
                trailing = {
                    PopupMenuButton(
                        items = listOf(
                            R.string.es_select_all,
                            R.string.es_deselect_all
                        ),
                        onSelected = {
                            when (it) {
                                R.string.es_select_all -> viewModel.selectAllClicked()
                                R.string.es_deselect_all -> viewModel.deselectAllClicked()
                            }
                        },
                        item = {
                            Text(stringResource(it))
                        }
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
                            onClick = { viewModel.appClicked(app) }
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
    SimpleListItem(
        title = { Text(app.info.appName) },
        leading = {
            SizedBox(size = 40.dp) {
                Center {
                    Image(data = AppIcon(app.info.packageName)) {
                        Icon(image = it, style = AvatarIconStyle())
                    }
                }
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
    private val checkedApps = BroadcastChannel<Set<String>>(Channel.CONFLATED)

    init {
        viewModelScope.launch(dispatchers.io) {
            val appsFlow = flowOf {
                appStore.getInstalledApps().filter(appFilter)
            }

            appsFlow.combine(checkedApps.asFlow()) { apps, checked ->
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
            .onEach { checkedApps.offer(it) }
            .launchIn(checkedAppsScope.coroutineScope)
    }

    fun detach() {
        checkedAppsScope.clear()
        onCheckedAppsChanged = null
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
        checkedApps
            .asFlow()
            .first()
            .toMutableSet()
            .apply(reducer)
            .let { onCheckedAppsChanged?.invoke(it) }
    }
}

internal data class CheckableAppsState(
    val apps: Async<List<CheckableApp>> = Uninitialized
)

internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

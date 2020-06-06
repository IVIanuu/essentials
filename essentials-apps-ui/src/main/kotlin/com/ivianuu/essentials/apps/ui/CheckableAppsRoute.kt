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
import androidx.compose.onCommit
import androidx.ui.core.Modifier
import androidx.ui.layout.preferredSize
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.coroutines.flowOf
import com.ivianuu.essentials.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.common.RenderAsyncList
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.Checkbox
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuButton
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Uninitialized
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun CheckableAppsScreen(
    checkedAppsFlow: Flow<Set<String>>,
    onCheckedAppsChanged: suspend (Set<String>) -> Unit,
    appBarTitle: String,
    appFilter: AppFilter = DefaultAppFilter
) {
    val viewModelFactory = inject<@Provider (AppFilter) -> CheckableAppsViewModel>()
    val viewModel = viewModel { viewModelFactory(appFilter) }

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
                                title = stringResource(R.string.es_select_all),
                                onSelected = { viewModel.selectAllClicked() }
                            ),
                            PopupMenu.Item(
                                title = stringResource(R.string.es_deselect_all),
                                onSelected = { viewModel.deselectAllClicked() }
                            )
                        )
                    )
                }
            )
        },
        body = {
            RenderAsyncList(
                state = viewModel.state.apps,
                successItemCallback = { app ->
                    CheckableApp(
                        app = app,
                        onClick = { viewModel.appClicked(app) }
                    )
                }
            )
        }
    )
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
                modifier = Modifier.preferredSize(40.dp)
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

@Transient
internal class CheckableAppsViewModel(
    private val appFilter: @Assisted AppFilter,
    private val appStore: AppStore
) : MvRxViewModel<CheckableAppsState>(CheckableAppsState()) {

    private var onCheckedAppsChanged: (suspend (Set<String>) -> Unit)? = null
    private var checkedAppsJob: Job? = null
    private val checkedApps = MutableStateFlow(emptySet<String>())

    init {
        coroutineScope.launch {
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
        onCheckedAppsChanged: suspend (Set<String>) -> Unit
    ) {
        this.onCheckedAppsChanged = onCheckedAppsChanged

        checkedAppsJob = checkedAppsFlow
            .onEach { checkedApps.value = it }
            .launchIn(coroutineScope)
    }

    fun detach() {
        checkedAppsJob?.cancel()
        onCheckedAppsChanged = null
    }

    fun appClicked(app: CheckableApp) {
        coroutineScope.launch {
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
        coroutineScope.launch {
            state.apps()?.let { allApps ->
                pushNewCheckedApps { newApps ->
                    newApps += allApps.map { it.info.packageName }
                }
            }
        }
    }

    fun deselectAllClicked() {
        coroutineScope.launch {
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
    val apps: Async<List<CheckableApp>> = Uninitialized()
)

@Immutable
internal data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

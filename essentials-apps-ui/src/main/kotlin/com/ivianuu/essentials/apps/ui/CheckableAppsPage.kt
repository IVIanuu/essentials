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
import androidx.ui.foundation.Text
import androidx.ui.layout.size
import androidx.ui.material.Checkbox
import androidx.ui.unit.dp
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.getInstalledApps
import com.ivianuu.essentials.apps.ui.CheckableAppsAction.AppClicked
import com.ivianuu.essentials.apps.ui.CheckableAppsAction.DeselectAllClicked
import com.ivianuu.essentials.apps.ui.CheckableAppsAction.SelectAllClicked
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.setState
import com.ivianuu.essentials.store.store
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
import com.ivianuu.essentials.ui.resource.map
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.ui.store.executeIn
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

@Reader
@Composable
fun CheckableAppsPage(
    checkedApps: Set<String>,
    onCheckedAppsChanged: suspend (Set<String>) -> Unit,
    appBarTitle: String,
    appFilter: AppFilter = DefaultAppFilter
) {
    val (state, dispatch) = rememberStore(
        appFilter, onCheckedAppsChanged
    ) {
        checkableAppsStore(appFilter, onCheckedAppsChanged)
    }

    onCommit(checkedApps) {
        dispatch(CheckableAppsAction.CheckedAppsChanged(checkedApps))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appBarTitle) },
                actions = {
                    PopupMenuButton(
                        items = listOf(
                            PopupMenu.Item(onSelected = { dispatch(SelectAllClicked) }) {
                                Text(R.string.es_select_all)
                            },
                            PopupMenu.Item(onSelected = { dispatch(DeselectAllClicked) }) {
                                Text(R.string.es_deselect_all)
                            }
                        )
                    )
                }
            )
        }
    ) {
        ResourceLazyColumnItems(state.checkableApps) { app ->
            CheckableApp(
                app = app,
                onClick = { dispatch(AppClicked(app)) }
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

@Reader
private fun checkableAppsStore(
    appFilter: AppFilter,
    onCheckedAppsChanged: suspend (Set<String>) -> Unit
) = store<CheckableAppsState, CheckableAppsAction>(
    CheckableAppsState()
) {
    execute(
        block = {
            getInstalledApps()
                .filter(appFilter)
        },
        reducer = { copy(apps = it) }
    )

    onEachAction { action ->
        suspend fun pushNewCheckedApps(reducer: (MutableSet<String>) -> Unit) {
            val newCheckedApps = state.value.checkableApps()!!
                .filter { it.isChecked }
                .map { it.info.packageName }
                .toMutableSet()
                .apply(reducer)
            onCheckedAppsChanged(newCheckedApps)
        }

        when (action) {
            is CheckableAppsAction.CheckedAppsChanged -> {
                setState {
                    copy(checkedApps = action.checkedApps)
                }
            }
            is AppClicked -> {
                pushNewCheckedApps {
                    if (!action.app.isChecked) {
                        it += action.app.info.packageName
                    } else {
                        it -= action.app.info.packageName
                    }
                }
            }
            SelectAllClicked -> {
                state.value.apps()?.let { allApps ->
                    pushNewCheckedApps { newApps ->
                        newApps += allApps.map { it.packageName }
                    }
                }
            }
            DeselectAllClicked -> {
                pushNewCheckedApps { it.clear() }
            }
        }.exhaustive
    }
}

@Immutable
private data class CheckableApp(
    val info: AppInfo,
    val isChecked: Boolean
)

@Immutable
private data class CheckableAppsState(
    val apps: Resource<List<AppInfo>> = Idle,
    val checkedApps: Set<String> = emptySet()
) {
    val checkableApps = apps.map { apps ->
        apps.map { app ->
            CheckableApp(
                info = app,
                isChecked = app.packageName in checkedApps
            )
        }
    }
}

private sealed class CheckableAppsAction {
    data class CheckedAppsChanged(val checkedApps: Set<String>) : CheckableAppsAction()
    data class AppClicked(val app: CheckableApp) : CheckableAppsAction()
    object SelectAllClicked : CheckableAppsAction()
    object DeselectAllClicked : CheckableAppsAction()
}

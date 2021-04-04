/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.android.prefs.Pref
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.hidenavbar.NavBarRotationMode
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.hidenavbar.ui.NavBarAction.UpdateHideNavBar
import com.ivianuu.essentials.hidenavbar.ui.NavBarAction.UpdateNavBarRotationMode
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NavBarKey : Key<Nothing>

@Given
val navBarKeyModule = KeyModule<NavBarKey>()

@Given
fun navBarUi(
    @Given stateFlow: StateFlow<NavBarState>,
    @Given dispatch: Collector<NavBarAction>
): KeyUi<NavBarKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_nav_bar_title)) }) }
    ) {
        val state by stateFlow.collectAsState()
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = state.hideNavBar,
                    onValueChange = { dispatch(UpdateHideNavBar(it)) },
                    title = { Text(stringResource(R.string.es_pref_hide_nav_bar)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode)) },
                    subtitle = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode_summary)) },
                    modifier = Modifier.interactive(state.canChangeNavBarRotationMode),
                    onClick = { dispatch(UpdateNavBarRotationMode) }
                )
            }
        }
    }
}

data class NavBarState(
    val hideNavBar: Boolean = false,
    val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT
) {
    val canChangeNavBarRotationMode: Boolean
        get() = hideNavBar
}

sealed class NavBarAction {
    data class UpdateHideNavBar(val value: Boolean) : NavBarAction()
    object UpdateNavBarRotationMode : NavBarAction()
}

@Given
fun navBarState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial NavBarState = NavBarState(),
    @Given actions: Flow<NavBarAction>,
    @Given navigator: Navigator,
    @Given permissionRequester: PermissionRequester,
    @Given pref: Pref<NavBarPrefs>,
    @Given resourceProvider: ResourceProvider
): @Scoped<KeyUiGivenScope> StateFlow<NavBarState> = scope.state(initial) {
    pref
        .onEach {
            update {
                copy(hideNavBar = it.hideNavBar, navBarRotationMode = it.navBarRotationMode)
            }
        }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateHideNavBar>()
        .onEach { action ->
            if (!action.value) {
                pref.update { copy(hideNavBar = false) }
            } else if (permissionRequester(listOf(typeKeyOf<NavBarPermission>()))) {
                pref.update { copy(hideNavBar = action.value) }
            } else Unit
        }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateNavBarRotationMode>()
        .onEach {
            navigator.pushForResult(
                SingleChoiceListKey(
                    items = NavBarRotationMode.values()
                        .map { mode ->
                            SingleChoiceListKey.Item(
                                title = resourceProvider.string(mode.titleRes),
                                value = mode
                            )
                        },
                    selectedItem = state.first().navBarRotationMode,
                    title = resourceProvider.string(R.string.es_pref_nav_bar_rotation_mode)
                )
            )?.let { newRotationMode ->
                pref.update { copy(navBarRotationMode = newRotationMode) }
            }
        }
        .launchIn(this)
}

@Given
val navBarActions: @Scoped<KeyUiGivenScope> MutableSharedFlow<NavBarAction>
    get() = EventFlow()

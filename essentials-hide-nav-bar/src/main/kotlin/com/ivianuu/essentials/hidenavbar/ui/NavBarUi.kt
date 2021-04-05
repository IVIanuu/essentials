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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.hidenavbar.NavBarRotationMode
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.StateKeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class NavBarKey : Key<Nothing>

@Given
val navBarUi: StateKeyUi<NavBarKey, NavBarViewModel, NavBarState> = { viewModel, state ->
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_nav_bar_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = state.hideNavBar,
                    onValueChange = { viewModel.updateHideNavBar(it) },
                    title = { Text(stringResource(R.string.es_pref_hide_nav_bar)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode)) },
                    subtitle = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode_summary)) },
                    modifier = Modifier.interactive(state.canChangeNavBarRotationMode),
                    onClick = { viewModel.updateNavBarRotationMode() }
                )
            }
        }
    }
}

data class NavBarState(
    val hideNavBar: Boolean = false,
    val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT
) : State() {
    val canChangeNavBarRotationMode: Boolean
        get() = hideNavBar
}

@Scoped<KeyUiGivenScope>
@Given
class NavBarViewModel(
    @Given private val navigator: Navigator,
    @Given private val permissionRequester: PermissionRequester,
    @Given private val pref: DataStore<NavBarPrefs>,
    @Given private val resourceProvider: ResourceProvider,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, NavBarState>
) : StateFlow<NavBarState> by store {
    init {
        pref
            .updateIn(store) {
                copy(hideNavBar = it.hideNavBar, navBarRotationMode = it.navBarRotationMode)
            }
    }
    fun updateHideNavBar(value: Boolean) = store.effect {
        if (!value) {
            pref.update { copy(hideNavBar = false) }
        } else if (permissionRequester(listOf(typeKeyOf<NavBarPermission>()))) {
            pref.update { copy(hideNavBar = value) }
        } else Unit
    }
    fun updateNavBarRotationMode() = store.effect {
        navigator.pushForResult(
            SingleChoiceListKey(
                items = NavBarRotationMode.values()
                    .map { mode ->
                        SingleChoiceListKey.Item(
                            title = resourceProvider.string(mode.titleRes),
                            value = mode
                        )
                    },
                selectedItem = store.first().navBarRotationMode,
                title = resourceProvider.string(R.string.es_pref_nav_bar_rotation_mode)
            )
        )?.let { newRotationMode ->
            pref.update { copy(navBarRotationMode = newRotationMode) }
        }
    }
}

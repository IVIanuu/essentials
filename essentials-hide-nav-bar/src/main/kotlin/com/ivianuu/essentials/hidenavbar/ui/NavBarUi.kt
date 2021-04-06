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
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.hidenavbar.NavBarRotationMode
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.hidenavbar.ui.NavBarAction.*
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.effectOn
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pushForResult
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

class NavBarKey : Key<Nothing>

@Given
val navBarUi: StoreKeyUi<NavBarKey, NavBarState, NavBarAction> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_nav_bar_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = state.hideNavBar,
                    onValueChange = { emit(UpdateHideNavBar(it)) },
                    title = { Text(stringResource(R.string.es_pref_hide_nav_bar)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode)) },
                    subtitle = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode_summary)) },
                    modifier = Modifier.interactive(state.canChangeNavBarRotationMode),
                    onClick = { emit(UpdateNavBarRotationMode) }
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
fun navBarStore(
    @Given navigator: Collector<NavigationAction>,
    @Given permissionRequester: PermissionRequester,
    @Given pref: Store<NavBarPrefs, ValueAction<NavBarPrefs>>,
    @Given resourceProvider: ResourceProvider,
): StoreBuilder<KeyUiGivenScope, NavBarState, NavBarAction> = {
    pref.update {
        copy(hideNavBar = it.hideNavBar, navBarRotationMode = it.navBarRotationMode)
    }
    effectOn<UpdateHideNavBar> { action ->
        if (!action.value) {
            pref.update { copy(hideNavBar = false) }
        } else if (permissionRequester(listOf(typeKeyOf<NavBarPermission>()))) {
            pref.update { copy(hideNavBar = action.value) }
        } else Unit
    }
    effectOn<UpdateNavBarRotationMode> {
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
}

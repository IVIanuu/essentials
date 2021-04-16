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
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.hidenavbar.NavBarRotationMode
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
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
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

class NavBarKey : Key<Nothing>

@Given
val navBarUi: StateKeyUi<NavBarKey, NavBarState> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_nav_bar_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = state.hideNavBar,
                    onValueChange = state.updateHideNavBar,
                    title = { Text(stringResource(R.string.es_pref_hide_nav_bar)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode)) },
                    subtitle = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode_summary)) },
                    modifier = Modifier.interactive(state.canChangeNavBarRotationMode),
                    onClick = state.updateNavBarRotationMode
                )
            }
        }
    }
}

@Optics
data class NavBarState(
    val hideNavBar: Boolean = false,
    val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT,
    val updateHideNavBar: (Boolean) -> Unit = {},
    val updateNavBarRotationMode: () -> Unit = {}
) {
    val canChangeNavBarRotationMode: Boolean
        get() = hideNavBar
}

@Given
fun navBarState(
    @Given navigator: Navigator,
    @Given permissionRequester: PermissionRequester,
    @Given pref: DataStore<NavBarPrefs>,
    @Given stringResource: StringResourceProvider,
): StateBuilder<KeyUiGivenScope, NavBarState> = {
    pref.data.updateIn(this) {
        copy(hideNavBar = it.hideNavBar, navBarRotationMode = it.navBarRotationMode)
    }
    action(NavBarState.updateHideNavBar()) { value ->
        if (!value) {
            pref.updateData { copy(hideNavBar = false) }
        } else if (permissionRequester(listOf(typeKeyOf<NavBarPermission>()))) {
            pref.updateData { copy(hideNavBar = value) }
        }
    }
    action(NavBarState.updateNavBarRotationMode()) {
        navigator.pushForResult(
            SingleChoiceListKey(
                items = NavBarRotationMode.values()
                    .map { mode ->
                        SingleChoiceListKey.Item(
                            title = stringResource(mode.titleRes, emptyList()),
                            value = mode
                        )
                    },
                selectedItem = state.first().navBarRotationMode,
                title = stringResource(R.string.es_pref_nav_bar_rotation_mode, emptyList())
            )
        )?.let { newRotationMode ->
            pref.updateData { copy(navBarRotationMode = newRotationMode) }
        }
    }
}

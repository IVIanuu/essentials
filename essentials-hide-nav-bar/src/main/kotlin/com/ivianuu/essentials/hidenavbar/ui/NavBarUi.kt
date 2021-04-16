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

import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.hidenavbar.*
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

class NavBarKey : Key<Nothing>

@Given
val navBarUi: ModelKeyUi<NavBarKey, NavBarModel> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_nav_bar_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                SwitchListItem(
                    value = model.hideNavBar,
                    onValueChange = model.updateHideNavBar,
                    title = { Text(stringResource(R.string.es_pref_hide_nav_bar)) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode)) },
                    subtitle = { Text(stringResource(R.string.es_pref_nav_bar_rotation_mode_summary)) },
                    modifier = Modifier.interactive(model.canChangeNavBarRotationMode),
                    onClick = model.updateNavBarRotationMode
                )
            }
        }
    }
}

@Optics
data class NavBarModel(
    val hideNavBar: Boolean = false,
    val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT,
    val updateHideNavBar: (Boolean) -> Unit = {},
    val updateNavBarRotationMode: () -> Unit = {}
) {
    val canChangeNavBarRotationMode: Boolean
        get() = hideNavBar
}

@Given
fun navBarModel(
    @Given navigator: Navigator,
    @Given permissionRequester: PermissionRequester,
    @Given pref: DataStore<NavBarPrefs>,
    @Given stringResource: StringResourceProvider,
): StateBuilder<KeyUiGivenScope, NavBarModel> = {
    pref.data.update {
        copy(hideNavBar = it.hideNavBar, navBarRotationMode = it.navBarRotationMode)
    }
    action(NavBarModel.updateHideNavBar()) { value ->
        if (!value) {
            pref.updateData { copy(hideNavBar = false) }
        } else if (permissionRequester(listOf(typeKeyOf<NavBarPermission>()))) {
            pref.updateData { copy(hideNavBar = value) }
        }
    }
    action(NavBarModel.updateNavBarRotationMode()) {
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

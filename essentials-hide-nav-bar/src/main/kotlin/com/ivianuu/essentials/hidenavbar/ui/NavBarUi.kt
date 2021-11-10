/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.hidenavbar.NavBarRotationMode
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

object NavBarKey : Key<Unit>

@Provide val navBarUi: ModelKeyUi<NavBarKey, NavBarModel> = {
  SimpleListScreen(R.string.es_nav_bar_title) {
    item {
      SwitchListItem(
        value = model.hideNavBar,
        onValueChange = model.updateHideNavBar,
        title = { Text(R.string.es_pref_hide_nav_bar) }
      )
    }
    item {
      ListItem(
        modifier = Modifier
          .clickable(onClick = model.updateNavBarRotationMode)
          .interactive(model.canChangeNavBarRotationMode),
        title = { Text(R.string.es_pref_nav_bar_rotation_mode) },
        subtitle = { Text(R.string.es_pref_nav_bar_rotation_mode_summary) }
      )
    }
  }
}

@Optics data class NavBarModel(
  val hideNavBar: Boolean = false,
  val navBarRotationMode: NavBarRotationMode = NavBarRotationMode.NOUGAT,
  val updateHideNavBar: (Boolean) -> Unit = {},
  val updateNavBarRotationMode: () -> Unit = {}
) {
  val canChangeNavBarRotationMode: Boolean
    get() = hideNavBar
}

@Provide fun navBarModel(
  permissionRequester: PermissionRequester,
  pref: DataStore<NavBarPrefs>,
  RP: ResourceProvider,
  ctx: KeyUiContext<NavBarKey>
) = state(NavBarModel()) {
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
    ctx.navigator.push(
      SingleChoiceListKey(
        items = NavBarRotationMode.values()
          .map { mode ->
            SingleChoiceListKey.Item(
              title = loadResource(mode.titleRes),
              value = mode
            )
          },
        selectedItem = state.first().navBarRotationMode,
        title = loadResource(R.string.es_pref_nav_bar_rotation_mode)
      )
    )?.let { newRotationMode ->
      pref.updateData { copy(navBarRotationMode = newRotationMode) }
    }
  }
}

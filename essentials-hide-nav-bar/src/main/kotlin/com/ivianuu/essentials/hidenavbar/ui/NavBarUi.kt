/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.hidenavbar.NavBarPermission
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.hidenavbar.NavBarRotationMode
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.valueFromFlow
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

object NavBarKey : Key<Unit>

@Provide val navBarUi = ModelKeyUi<NavBarKey, NavBarModel> {
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

data class NavBarModel(
  val hideNavBar: Boolean,
  val navBarRotationMode: NavBarRotationMode,
  val updateHideNavBar: (Boolean) -> Unit,
  val updateNavBarRotationMode: () -> Unit
) {
  val canChangeNavBarRotationMode: Boolean
    get() = hideNavBar
}

@Provide @Composable fun navBarModel(
  permissionRequester: PermissionRequester,
  pref: DataStore<NavBarPrefs>,
  RP: ResourceProvider,
  ctx: KeyUiContext<NavBarKey>
): NavBarModel {
  val prefs = valueFromFlow(NavBarPrefs()) { pref.data }
  return NavBarModel(
    hideNavBar = prefs.hideNavBar,
    navBarRotationMode = prefs.navBarRotationMode,
    updateHideNavBar = action { value ->
      if (!value) {
        pref.updateData { copy(hideNavBar = false) }
      } else if (permissionRequester(listOf(typeKeyOf<NavBarPermission>()))) {
        pref.updateData { copy(hideNavBar = value) }
      }
    },
    updateNavBarRotationMode = action {
      ctx.navigator.push(
        SingleChoiceListKey(
          items = NavBarRotationMode.values()
            .map { mode ->
              SingleChoiceListKey.Item(
                title = loadResource(mode.titleRes),
                value = mode
              )
            },
          selectedItem = prefs.navBarRotationMode,
          title = loadResource(R.string.es_pref_nav_bar_rotation_mode)
        )
      )?.let { newRotationMode ->
        pref.updateData { copy(navBarRotationMode = newRotationMode) }
      }
    }
  )
}

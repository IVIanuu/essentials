/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.bind
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.common.interactive
import com.ivianuu.essentials.ui.dialog.SingleChoiceListKey
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

object NavBarKey : Key<Unit>

@Provide val navBarUi = ModelKeyUi<NavBarKey, NavBarModel> {
  SimpleListScreen(R.string.es_nav_bar_title) {
    item {
      SwitchListItem(
        value = hideNavBar,
        onValueChange = updateHideNavBar,
        title = { Text(R.string.es_pref_hide_nav_bar) }
      )
    }
    item {
      ListItem(
        modifier = Modifier
          .clickable(onClick = updateNavBarRotationMode)
          .interactive(canChangeNavBarRotationMode),
        title = { Text(R.string.es_pref_nav_bar_rotation_mode) },
        subtitle = { Text(R.string.es_pref_nav_bar_rotation_mode_summary) }
      )
    }
  }
}

data class NavBarModel(
  val hideNavBar: Boolean,
  val updateHideNavBar: (Boolean) -> Unit,
  val navBarRotationMode: NavBarRotationMode,
  val updateNavBarRotationMode: () -> Unit
) {
  val canChangeNavBarRotationMode: Boolean
    get() = hideNavBar
}

context(KeyUiContext<NavBarKey>, PermissionManager, ResourceProvider) @Provide fun navBarModel(
  pref: DataStore<NavBarPrefs>
) = Model {
  val prefs = pref.data.bind(NavBarPrefs())
  NavBarModel(
    hideNavBar = prefs.hideNavBar,
    updateHideNavBar = action { value ->
      if (!value) {
        pref.updateData { copy(hideNavBar = false) }
      } else if (requestPermissions(listOf(typeKeyOf<NavBarPermission>()))) {
        pref.updateData { copy(hideNavBar = value) }
      }
    },
    navBarRotationMode = prefs.navBarRotationMode,
    updateNavBarRotationMode = action {
      navigator.push(
        SingleChoiceListKey(
          items = NavBarRotationMode.values().toList(),
          selectedItem = prefs.navBarRotationMode
        ) { loadResource(titleRes) }
      )?.let { newRotationMode ->
        pref.updateData { copy(navBarRotationMode = newRotationMode) }
      }
    }
  )
}

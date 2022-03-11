/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.hidenavbar.*
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

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

@Provide fun navBarModel(
  permissionRequester: PermissionRequester,
  pref: DataStore<NavBarPrefs>,
  RP: ResourceProvider,
  ctx: KeyUiContext<NavBarKey>
) = Model {
  val prefs = pref.data.bind(NavBarPrefs())
  NavBarModel(
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

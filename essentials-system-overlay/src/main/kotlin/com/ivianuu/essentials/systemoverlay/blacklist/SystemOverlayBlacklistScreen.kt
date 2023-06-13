/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide

class SystemOverlayBlacklistScreen(val systemOverlayName: String) : Screen<Unit>

@Provide
val systemOverlayBlacklistUi =
  Ui<SystemOverlayBlacklistScreen, SystemOverlayBlacklistModel> { model ->
    SimpleListScreen(R.string.es_system_overlay_blacklist_title) {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = model.openAppBlacklistSettings),
          title = {
            Text(R.string.es_pref_system_overlay_app_blacklist, model.systemOverlayName)
          }
        )
      }

    item {
      SwitchListItem(
        value = model.disableOnKeyboard,
        onValueChange = model.updateDisableOnKeyboard,
        title = { Text(R.string.es_pref_disable_on_keyboard) },
        subtitle = {
          Text(R.string.es_pref_disable_on_keyboard_summary, model.systemOverlayName)
        }
      )
    }

    item {
      SwitchListItem(
        value = model.disableOnLockScreen,
        onValueChange = model.updateDisableOnLockScreen,
        title = { Text(R.string.es_pref_disable_on_lock_screen) },
        subtitle = {
          Text(R.string.es_pref_disable_on_lock_screen_summary, model.systemOverlayName)
        }
      )
    }

    item {
      SwitchListItem(
        value = model.disableOnSecureScreens,
        onValueChange = model.updateDisableOnSecureScreens,
        title = { Text(R.string.es_pref_disable_on_secure_screens) },
        subtitle = {
          Text(R.string.es_pref_disable_on_secure_screens_summary, model.systemOverlayName)
        }
      )
    }
  }
}

data class SystemOverlayBlacklistModel(
  val systemOverlayName: String,
  val disableOnKeyboard: Boolean,
  val updateDisableOnKeyboard: (Boolean) -> Unit,
  val disableOnLockScreen: Boolean,
  val updateDisableOnLockScreen: (Boolean) -> Unit,
  val disableOnSecureScreens: Boolean,
  val updateDisableOnSecureScreens: (Boolean) -> Unit,
  val openAppBlacklistSettings: () -> Unit
)

@Provide fun systemOverlayBlacklistModel(
  navigator: Navigator,
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  screen: SystemOverlayBlacklistScreen
) = Model {
  val prefs = pref.data.bind(SystemOverlayBlacklistPrefs())
  SystemOverlayBlacklistModel(
    systemOverlayName = screen.systemOverlayName,
    disableOnKeyboard = prefs.disableOnKeyboard,
    updateDisableOnKeyboard = action { value ->
      pref.updateData { copy(disableOnKeyboard = value) }
    },
    disableOnLockScreen = prefs.disableOnLockScreen,
    updateDisableOnLockScreen = action { value ->
      pref.updateData { copy(disableOnLockScreen = value) }
    },
    disableOnSecureScreens = prefs.disableOnSecureScreens,
    updateDisableOnSecureScreens = action { value ->
      pref.updateData { copy(disableOnSecureScreens = value) }
    },
    openAppBlacklistSettings = action {
      navigator.push(SystemOverlayAppBlacklistScreen())
    }
  )
}

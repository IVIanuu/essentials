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
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide

class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Unit>

@Provide
val systemOverlayBlacklistUi = KeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistModel> {
  SimpleListScreen(R.string.es_system_overlay_blacklist_title) {
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = openAppBlacklistSettings),
        title = {
          Text(R.string.es_pref_system_overlay_app_blacklist, systemOverlayName)
        }
      )
    }

    item {
      SwitchListItem(
        value = disableOnKeyboard,
        onValueChange = updateDisableOnKeyboard,
        title = { Text(R.string.es_pref_disable_on_keyboard) },
        subtitle = {
          Text(R.string.es_pref_disable_on_keyboard_summary, systemOverlayName)
        }
      )
    }

    item {
      SwitchListItem(
        value = disableOnLockScreen,
        onValueChange = updateDisableOnLockScreen,
        title = { Text(R.string.es_pref_disable_on_lock_screen) },
        subtitle = {
          Text(R.string.es_pref_disable_on_lock_screen_summary, systemOverlayName)
        }
      )
    }

    item {
      SwitchListItem(
        value = disableOnSecureScreens,
        onValueChange = updateDisableOnSecureScreens,
        title = { Text(R.string.es_pref_disable_on_secure_screens) },
        subtitle = {
          Text(R.string.es_pref_disable_on_secure_screens_summary, systemOverlayName)
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
  ctx: KeyUiContext<SystemOverlayBlacklistKey>,
  pref: DataStore<SystemOverlayBlacklistPrefs>
) = Model {
  val prefs = pref.data.bind(SystemOverlayBlacklistPrefs())
  SystemOverlayBlacklistModel(
    systemOverlayName = ctx.key.systemOverlayName,
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
      ctx.navigator.push(SystemOverlayAppBlacklistKey())
    }
  )
}

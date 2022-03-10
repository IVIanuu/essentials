/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*

data class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Unit>

@Provide
val systemOverlayBlacklistUi = ModelKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistModel> {
  SimpleListScreen(R.string.es_system_overlay_blacklist_title) {
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.openAppBlacklistSettings),
        title = {
          Text(
            R.string.es_pref_system_overlay_app_blacklist,
            model.systemOverlayName
          )
        }
      )
    }

    item {
      SwitchListItem(
        value = model.disableOnKeyboard,
        onValueChange = model.updateDisableOnKeyboard,
        title = { Text(R.string.es_pref_disable_on_keyboard) },
        subtitle = {
          Text(
            R.string.es_pref_disable_on_keyboard_summary,
            model.systemOverlayName
          )
        }
      )
    }

    item {
      SwitchListItem(
        value = model.disableOnLockScreen,
        onValueChange = model.updateDisableOnLockScreen,
        title = { Text(R.string.es_pref_disable_on_lock_screen) },
        subtitle = {
          Text(
            R.string.es_pref_disable_on_lock_screen_summary,
            model.systemOverlayName
          )
        }
      )
    }

    item {
      SwitchListItem(
        value = model.disableOnSecureScreens,
        onValueChange = model.updateDisableOnSecureScreens,
        title = { Text(R.string.es_pref_disable_on_secure_screens) },
        subtitle = {
          Text(
            R.string.es_pref_disable_on_secure_screens_summary,
            model.systemOverlayName
          )
        }
      )
    }
  }
}

data class SystemOverlayBlacklistModel(
  val systemOverlayName: String,
  val disableOnKeyboard: Boolean,
  val disableOnLockScreen: Boolean,
  val disableOnSecureScreens: Boolean,
  val openAppBlacklistSettings: () -> Unit,
  val updateDisableOnKeyboard: (Boolean) -> Unit,
  val updateDisableOnLockScreen: (Boolean) -> Unit,
  val updateDisableOnSecureScreens: (Boolean) -> Unit
)

@Provide fun systemOverlayBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  ctx: KeyUiContext<SystemOverlayBlacklistKey>
): @Composable () -> SystemOverlayBlacklistModel = {
  val prefs = pref.data.bind(SystemOverlayBlacklistPrefs())
  SystemOverlayBlacklistModel(
    systemOverlayName = ctx.key.systemOverlayName,
    disableOnKeyboard = prefs.disableOnKeyboard,
    disableOnLockScreen = prefs.disableOnLockScreen,
    disableOnSecureScreens = prefs.disableOnSecureScreens,
    openAppBlacklistSettings = action {
      ctx.navigator.push(SystemOverlayAppBlacklistKey)
    },
    updateDisableOnKeyboard = action { value ->
      pref.updateData { copy(disableOnKeyboard = value) }
    },
    updateDisableOnLockScreen = action { value ->
      pref.updateData { copy(disableOnLockScreen = value) }
    },
    updateDisableOnSecureScreens = action { value ->
      pref.updateData { copy(disableOnSecureScreens = value) }
    }
  )
}

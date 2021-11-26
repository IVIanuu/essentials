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

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide

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

@Provide @Composable fun systemOverlayBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  ctx: KeyUiContext<SystemOverlayBlacklistKey>
): SystemOverlayBlacklistModel {
  val prefs = valueFromFlow(SystemOverlayBlacklistPrefs()) { pref.data }
  return SystemOverlayBlacklistModel(
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

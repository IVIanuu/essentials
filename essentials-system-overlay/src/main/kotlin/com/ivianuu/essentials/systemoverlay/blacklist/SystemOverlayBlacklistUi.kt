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

import androidx.compose.material.Text
import com.ivianuu.essentials.Initial
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.prefs.CheckboxListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

data class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Unit>

@Provide
val systemOverlayBlacklistUi: ModelKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistModel> = {
  SimpleListScreen(R.string.es_system_overlay_blacklist_title) {
    item {
      ListItem(
        title = {
          Text(
            R.string.es_pref_system_overlay_app_blacklist,
            model.systemOverlayName
          )
        },
        onClick = model.openAppBlacklistSettings
      )
    }

    item {
      CheckboxListItem(
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
      CheckboxListItem(
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
      CheckboxListItem(
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

@Optics data class SystemOverlayBlacklistModel(
  val systemOverlayName: String,
  val disableOnKeyboard: Boolean = false,
  val disableOnLockScreen: Boolean = false,
  val disableOnSecureScreens: Boolean = false,
  val openAppBlacklistSettings: () -> Unit = {},
  val updateDisableOnKeyboard: (Boolean) -> Unit = {},
  val updateDisableOnLockScreen: (Boolean) -> Unit = {},
  val updateDisableOnSecureScreens: (Boolean) -> Unit = {}
) {
  companion object {
    @Provide fun initial(
      key: SystemOverlayBlacklistKey
    ): @Initial SystemOverlayBlacklistModel = SystemOverlayBlacklistModel(
      systemOverlayName = key.systemOverlayName
    )
  }
}

@Provide fun systemOverlayBlacklistModel(
  initial: @Initial SystemOverlayBlacklistModel,
  navigator: Navigator,
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<SystemOverlayBlacklistModel> = scope.state(initial) {
  pref.data.update {
    copy(
      disableOnKeyboard = it.disableOnKeyboard,
      disableOnLockScreen = it.disableOnLockScreen,
      disableOnSecureScreens = it.disableOnSecureScreens
    )
  }
  action(SystemOverlayBlacklistModel.openAppBlacklistSettings()) {
    navigator.push(SystemOverlayAppBlacklistKey)
  }
  action(SystemOverlayBlacklistModel.updateDisableOnKeyboard()) { value ->
    pref.updateData { copy(disableOnKeyboard = value) }
  }
  action(SystemOverlayBlacklistModel.updateDisableOnLockScreen()) { value ->
    pref.updateData { copy(disableOnLockScreen = value) }
  }
  action(SystemOverlayBlacklistModel.updateDisableOnSecureScreens()) { value ->
    pref.updateData { copy(disableOnSecureScreens = value) }
  }
}

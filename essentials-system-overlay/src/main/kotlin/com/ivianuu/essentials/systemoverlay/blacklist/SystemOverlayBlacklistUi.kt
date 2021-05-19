package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.ui.res.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

data class SystemOverlayBlacklistKey(val systemOverlayName: String) : Key<Nothing>

@Provide
val systemOverlayBlacklistUi: ModelKeyUi<SystemOverlayBlacklistKey, SystemOverlayBlacklistModel> = {
  Scaffold(
    topBar = { TopAppBar(title = { Text(stringResource(R.string.es_system_overlay_blacklist_title)) }) }
  ) {
    LazyColumn(contentPadding = localVerticalInsetsPadding()) {
      item {
        ListItem(
          title = {
            Text(
              stringResource(
                R.string.es_pref_system_overlay_app_blacklist,
                model.systemOverlayName
              )
            )
          },
          onClick = model.openAppBlacklistSettings
        )
      }

      item {
        CheckboxListItem(
          value = model.disableOnKeyboard,
          onValueChange = model.updateDisableOnKeyboard,
          title = { Text(stringResource(R.string.es_pref_disable_on_keyboard)) },
          subtitle = {
            Text(
              stringResource(
                R.string.es_pref_disable_on_keyboard_summary,
                model.systemOverlayName
              )
            )
          }
        )
      }

      item {
        CheckboxListItem(
          value = model.disableOnLockScreen,
          onValueChange = model.updateDisableOnLockScreen,
          title = { Text(stringResource(R.string.es_pref_disable_on_lock_screen)) },
          subtitle = {
            Text(
              stringResource(
                R.string.es_pref_disable_on_lock_screen_summary,
                model.systemOverlayName
              )
            )
          }
        )
      }

      item {
        CheckboxListItem(
          value = model.disableOnSecureScreens,
          onValueChange = model.updateDisableOnSecureScreens,
          title = { Text(stringResource(R.string.es_pref_disable_on_secure_screens)) },
          subtitle = {
            Text(
              stringResource(
                R.string.es_pref_disable_on_secure_screens_summary,
                model.systemOverlayName
              )
            )
          }
        )
      }
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
  scope: InjectCoroutineScope<KeyUiScope>
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

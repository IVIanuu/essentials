/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.map

object SystemOverlayAppBlacklistKey : Key<Unit>

@Provide fun systemOverlayAppBlacklistUi(
  checkableAppsPageFactory: (CheckableAppsParams) -> CheckableAppsScreen,
  T: ToastContext
) = ModelKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistModel> {
  remember {
    checkableAppsPageFactory(
      CheckableAppsParams(
        checkedApps = model.appBlacklist,
        onCheckedAppsChanged = model.updateAppBlacklist,
        appPredicate = DefaultAppPredicate,
        appBarTitle = loadResource(R.string.es_system_overlay_blacklist_title)
      )
    )
  }()
}

data class SystemOverlayAppBlacklistModel(
  val appBlacklist: @Composable () -> Set<String>,
  val updateAppBlacklist: (Set<String>) -> Unit = {}
)

@Provide @Composable fun systemOverlayAppBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  S: NamedCoroutineScope<KeyUiScope>
) = SystemOverlayAppBlacklistModel(
  appBlacklist = {
    valueFromFlow(emptySet()) { pref.data.map { it.appBlacklist } }
  },
  updateAppBlacklist = action { appBlacklist ->
    pref.updateData { copy(appBlacklist = appBlacklist) }
  }
)

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SystemOverlayAppBlacklistKey : Key<Unit>

@Provide fun systemOverlayAppBlacklistUi(
  checkableAppsPageFactory: (CheckableAppsParams) -> CheckableAppsScreen,
  T: ToastContext
) = ModelKeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistModel> {
  remember {
    checkableAppsPageFactory(
      CheckableAppsParams(
        checkedApps = appBlacklist,
        onCheckedAppsChanged = updateAppBlacklist,
        appPredicate = DefaultAppPredicate,
        appBarTitle = loadResource(R.string.es_system_overlay_blacklist_title)
      )
    )
  }()
}

data class SystemOverlayAppBlacklistModel(
  val appBlacklist: Flow<Set<String>>,
  val updateAppBlacklist: (Set<String>) -> Unit = {}
)

@Provide fun systemOverlayAppBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  ctx: KeyUiContext<SystemOverlayAppBlacklistKey>
) = Model {
  SystemOverlayAppBlacklistModel(
    appBlacklist = pref.data.map { it.appBlacklist },
    updateAppBlacklist = action { appBlacklist ->
      pref.updateData { copy(appBlacklist = appBlacklist) }
    }
  )
}

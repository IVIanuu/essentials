/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.apps.ui.DefaultAppPredicate
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsParams
import com.ivianuu.essentials.apps.ui.checkableapps.CheckableAppsScreen
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SystemOverlayAppBlacklistKey : Key<Unit>

@Provide fun systemOverlayAppBlacklistUi(
  checkableAppsPageFactory: (CheckableAppsParams) -> CheckableAppsScreen,
  resources: Resources
) = KeyUi<SystemOverlayAppBlacklistKey, SystemOverlayAppBlacklistModel> { model ->
  remember {
    checkableAppsPageFactory(
      CheckableAppsParams(
        checkedApps = model.appBlacklist,
        onCheckedAppsChanged = model.updateAppBlacklist,
        appPredicate = DefaultAppPredicate,
        appBarTitle = resources(R.string.es_system_overlay_blacklist_title)
      )
    )
  }()
}

data class SystemOverlayAppBlacklistModel(
  val appBlacklist: Flow<Set<String>>,
  val updateAppBlacklist: (Set<String>) -> Unit = {}
)

@Provide fun systemOverlayAppBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>
) = Model {
  SystemOverlayAppBlacklistModel(
    appBlacklist = pref.data.map { it.appBlacklist },
    updateAppBlacklist = action { appBlacklist ->
      pref.updateData { copy(appBlacklist = appBlacklist) }
    }
  )
}

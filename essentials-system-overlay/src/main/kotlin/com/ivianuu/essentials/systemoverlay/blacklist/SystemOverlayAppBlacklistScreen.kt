/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.remember
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.apps.CheckableAppsParams
import com.ivianuu.essentials.apps.CheckableAppsUi
import com.ivianuu.essentials.apps.DefaultAppPredicate
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SystemOverlayAppBlacklistScreen : Screen<Unit>

@Provide fun systemOverlayAppBlacklistUi(
  checkableAppsPageFactory: (CheckableAppsParams) -> CheckableAppsUi,
  resources: Resources
) = Ui<SystemOverlayAppBlacklistScreen, SystemOverlayAppBlacklistModel> { model ->
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

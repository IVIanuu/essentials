/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay.blacklist

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.checkableapps.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.systemoverlay.R
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

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
  val appBlacklist: Flow<Set<String>>,
  val updateAppBlacklist: (Set<String>) -> Unit = {}
)

@Provide fun systemOverlayAppBlacklistModel(
  pref: DataStore<SystemOverlayBlacklistPrefs>,
  ctx: KeyUiContext<SystemOverlayAppBlacklistKey>
): @Composable () -> SystemOverlayAppBlacklistModel = {
  SystemOverlayAppBlacklistModel(
    appBlacklist = pref.data.map { it.appBlacklist },
    updateAppBlacklist = action { appBlacklist ->
      pref.updateData { copy(appBlacklist = appBlacklist) }
    }
  )
}

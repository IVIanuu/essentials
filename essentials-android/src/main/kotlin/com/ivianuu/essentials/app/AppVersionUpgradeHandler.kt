/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.android.prefs.DataStoreModule
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

fun interface AppVersionUpgradeHandler : suspend (Int) -> Unit {
  companion object {
    @Provide val defaultHandlers: List<AppVersionUpgradeHandler>
      get() = emptyList()
  }
}

@Provide fun appVersionUpgradeWorker(
  buildInfo: BuildInfo,
  handlers: List<AppVersionUpgradeHandler>,
  pref: DataStore<AppVersionUpgradePrefs>,
  L: Logger
) = ScopeWorker<AppScope> {
  val prefs = pref.data.first()

  if (buildInfo.versionCode <= prefs.lastAppVersion) return@ScopeWorker

  log { "upgrade from app version ${prefs.lastAppVersion} to ${buildInfo.versionCode}" }

  handlers.parForEach {
    it(prefs.lastAppVersion)
  }

  pref.updateData { copy(lastAppVersion = buildInfo.versionCode) }
}

@Serializable data class AppVersionUpgradePrefs(val lastAppVersion: Int = 0) {
  companion object {
    @Provide val prefModule = DataStoreModule("version_upgrade_prefs") {
      AppVersionUpgradePrefs()
    }
  }
}

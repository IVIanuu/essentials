/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

fun interface AppVersionUpgradeHandler {
  suspend operator fun invoke(lastAppVersion: Int, appVersion: Int)

  companion object {
    @Provide val defaultHandlers get() = emptyList<AppVersionUpgradeHandler>()
  }
}

context(BuildInfo, Logger) @Provide fun appVersionUpgradeWorker(
  handlers: () -> List<AppVersionUpgradeHandler>,
  pref: DataStore<AppVersionUpgradePrefs>
) = ScopeWorker<AppScope> {
  val prefs = pref.data.first()

  if (versionCode <= prefs.lastAppVersion) return@ScopeWorker

  log { "upgrade from app version ${prefs.lastAppVersion} to $versionCode" }

  handlers().parForEach { it(prefs.lastAppVersion, versionCode) }

  pref.updateData { copy(lastAppVersion = versionCode) }
}

@Serializable data class AppVersionUpgradePrefs(val lastAppVersion: Int = 0) {
  companion object {
    @Provide val prefModule = PrefModule { AppVersionUpgradePrefs() }
  }
}

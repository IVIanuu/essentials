/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import arrow.fx.coroutines.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

fun interface AppVersionUpgradeHandler {
  suspend fun onAppVersionUpgrade(lastAppVersion: Int, appVersion: Int)

  @Provide companion object {
    @Provide val defaultHandlers get() = emptyList<AppVersionUpgradeHandler>()
  }
}

@Provide fun appVersionUpgradeWorker(
  appConfig: AppConfig,
  handlers: () -> List<AppVersionUpgradeHandler>,
  logger: Logger,
  pref: DataStore<AppVersionUpgradePrefs>
) = ScopeWorker<AppScope> {
  val prefs = pref.data.first()

  if (appConfig.versionCode <= prefs.lastAppVersion) return@ScopeWorker

  logger.d { "upgrade from app version ${prefs.lastAppVersion} to ${appConfig.versionCode}" }

  handlers().parMap { it.onAppVersionUpgrade(prefs.lastAppVersion, appConfig.versionCode) }

  pref.updateData { copy(lastAppVersion = appConfig.versionCode) }
}

@Serializable data class AppVersionUpgradePrefs(val lastAppVersion: Int = 0) {
  @Provide companion object {
    @Provide val dataStoreModule = DataStoreModule("app_version_upgrade") { AppVersionUpgradePrefs() }
  }
}

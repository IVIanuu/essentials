/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.DataStoreModule
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

@Provide fun appVersionUpgradeWorker(
  appConfig: AppConfig,
  handlers: () -> List<AppVersionUpgradeHandler>,
  logger: Logger,
  pref: DataStore<AppVersionUpgradePrefs>
) = ScopeWorker<AppScope> {
  val prefs = pref.data.first()

  if (appConfig.versionCode <= prefs.lastAppVersion) return@ScopeWorker

  logger.log { "upgrade from app version ${prefs.lastAppVersion} to ${appConfig.versionCode}" }

  handlers().parForEach { it(prefs.lastAppVersion, appConfig.versionCode) }

  pref.updateData { copy(lastAppVersion = appConfig.versionCode) }
}

@Serializable data class AppVersionUpgradePrefs(val lastAppVersion: Int = 0) {
  companion object {
    @Provide val dataStoreModule = DataStoreModule("app_version_upgrade") { AppVersionUpgradePrefs() }
  }
}

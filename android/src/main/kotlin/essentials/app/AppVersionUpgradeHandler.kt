/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import arrow.fx.coroutines.*
import essentials.*
import essentials.data.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

fun interface AppVersionUpgradeHandler {
  suspend fun onAppVersionUpgrade(lastAppVersion: Int?, appVersion: Int)

  @Provide companion object {
    @Provide val defaultHandlers get() = emptyList<AppVersionUpgradeHandler>()
  }
}

@Provide fun appVersionUpgradeWorker(
  appConfig: AppConfig,
  handlers: () -> List<AppVersionUpgradeHandler>,
  logger: Logger,
  preferencesStore: DataStore<Preferences>
) = ScopeComposition<AppScope> {
  LaunchedEffect(true) {
    val lastAppVersion = preferencesStore.data.first()[LastAppVersionPrefKey]

    if (lastAppVersion == null ||
      appConfig.versionCode <= lastAppVersion) return@LaunchedEffect

    logger.d { "upgrade from app version $lastAppVersion to ${appConfig.versionCode}" }

    handlers().parMap { it.onAppVersionUpgrade(lastAppVersion, appConfig.versionCode) }

    preferencesStore.edit { it[LastAppVersionPrefKey] = appConfig.versionCode }
  }
}

private val LastAppVersionPrefKey = intPreferencesKey("last_app_version")

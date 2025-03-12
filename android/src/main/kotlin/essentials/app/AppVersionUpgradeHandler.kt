/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import arrow.fx.coroutines.*
import essentials.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.flow.*

data class AppVersionUpgradeParams(val lastAppVersion: Int?, val appVersion: Int)
@Tag typealias AppVersionUpgradeResult = Unit
@Provide val defaultAppVersionUpgradeHandlers
  get() = emptyList<suspend (AppVersionUpgradeParams) -> AppVersionUpgradeResult>()

@Provide @Composable fun AppVersionUpgradeHandler(
  appConfig: AppConfig,
  handlers: () -> List<suspend (AppVersionUpgradeParams) -> AppVersionUpgradeResult>,
  logger: Logger,
  preferencesStore: DataStore<Preferences>
): ScopeCompositionResult<AppScope> {
  LaunchedEffect(true) {
    val lastAppVersion = preferencesStore.data.first()[LastAppVersionPrefKey]

    if (lastAppVersion == null ||
      appConfig.versionCode <= lastAppVersion) return@LaunchedEffect

    logger.d { "upgrade from app version $lastAppVersion to ${appConfig.versionCode}" }

    val params = AppVersionUpgradeParams(lastAppVersion, appConfig.versionCode)
    handlers().parMap { it(params) }

    preferencesStore.edit { it[LastAppVersionPrefKey] = appConfig.versionCode }
  }
}

private val LastAppVersionPrefKey = intPreferencesKey("last_app_version")

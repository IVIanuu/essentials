/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import androidx.compose.runtime.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.data.preferencesDataStore
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.flow.*

data class AppVersionUpgradeParams(val lastAppVersion: Int?, val appVersion: Int)
@Tag typealias AppVersionUpgradeResult = Unit
@Provide val defaultAppVersionUpgradeHandlers
  get() = emptyList<suspend (AppVersionUpgradeParams) -> AppVersionUpgradeResult>()

@Provide @Composable fun AppVersionUpgradeHandler(
  handlers: () -> List<suspend (AppVersionUpgradeParams) -> AppVersionUpgradeResult>,
  scope: Scope<AppScope> = inject
): ScopeContent<AppScope> {
  LaunchedEffect(true) {
    val lastAppVersion = preferencesDataStore().data.first()[LastAppVersionPrefKey]

    if (lastAppVersion == null ||
      appConfig().versionCode <= lastAppVersion) return@LaunchedEffect

    d { "upgrade from app version $lastAppVersion to ${appConfig().versionCode}" }

    val params = AppVersionUpgradeParams(lastAppVersion, appConfig().versionCode)
    handlers().parMap { it(params) }

    preferencesDataStore().edit { it[LastAppVersionPrefKey] = appConfig().versionCode }
  }
}

private val LastAppVersionPrefKey = intPreferencesKey("last_app_version")

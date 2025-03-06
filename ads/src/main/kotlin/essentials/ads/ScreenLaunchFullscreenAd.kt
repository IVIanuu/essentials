/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastFilter
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import essentials.ScopeComposition
import essentials.app.*
import essentials.data.*
import essentials.logging.*
import essentials.ui.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide object ScreenLaunchFullscreenAdFeature : AdFeature

data class ScreenLaunchFullscreenAdConfig(val screenLaunchToShowAdCount: Int = 4) {
  @Provide companion object {
    @Provide val defaultConfig get() = ScreenLaunchFullscreenAdConfig()
  }
}

@Provide fun screenLaunchFullScreenObserver(
  adsEnabledProducer: AdsEnabledProducer,
  adFeatureRepository: AdFeatureRepository,
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAdManager: FullScreenAdManager,
  logger: Logger,
  navigator: Navigator,
  preferencesStore: DataStore<Preferences>
) = ScopeComposition<UiScope> {
  if (adsEnabledProducer.adsEnabled())
    LaunchedEffect(true) {
      navigator.launchEvents(adFeatureRepository).collectLatest {
        val launchCount = preferencesStore
          .edit {
            it[FullScreenAdScreenLaunchCount] =
              (it[FullScreenAdScreenLaunchCount]?.inc() ?: 1)
          }[FullScreenAdScreenLaunchCount]!!
        logger.d { "screen launched $launchCount" }
        if (launchCount >= config.screenLaunchToShowAdCount) {
          logger.d { "try to show full screen ad $launchCount" }
          if (fullScreenAdManager.showAd())
            preferencesStore.edit { it[FullScreenAdScreenLaunchCount] = 0 }
        }
      }
    }
}

private val FullScreenAdScreenLaunchCount = intPreferencesKey("full_screen_ad_screen_launch_count")

private fun Navigator.launchEvents(adFeatureRepository: AdFeatureRepository): Flow<Screen<*>> {
  var lastBackStack = backStack
  return snapshotFlow { backStack }
    .mapNotNull { currentBackStack ->
      val launchedScreens = currentBackStack
        .fastFilter {
          it !in lastBackStack &&
              adFeatureRepository.isEnabled(it::class, ScreenLaunchFullscreenAdFeature)
        }
      (if (currentBackStack.size > 1 && launchedScreens.isNotEmpty()) launchedScreens.first()
      else null)
        .also { lastBackStack = currentBackStack }
    }
}

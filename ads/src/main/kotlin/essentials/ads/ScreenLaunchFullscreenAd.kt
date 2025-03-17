/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import essentials.*
import essentials.app.*
import essentials.logging.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide object ScreenLaunchFullscreenAdFeature : AdFeature

data class ScreenLaunchFullscreenAdConfig(val screenLaunchToShowAdCount: Int = 4) {
  @Provide companion object {
    @Provide val defaultConfig get() = ScreenLaunchFullscreenAdConfig()
  }
}

@Provide @Composable fun ScreenLaunchFullScreenAdManager(
  adsEnabled: AdsEnabled,
  adFeatureRepository: AdFeatureRepository,
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAds: FullScreenAds,
  logger: Logger = inject,
  navigator: Navigator,
  preferencesStore: DataStore<Preferences>
): ScopeContent<UiScope> {
  if (adsEnabled)
    LaunchedEffect(true) {
      navigator.launchEvents(adFeatureRepository).collectLatest {
        val launchCount = preferencesStore
          .edit {
            it[FullScreenAdScreenLaunchCount] =
              (it[FullScreenAdScreenLaunchCount]?.inc() ?: 1)
          }[FullScreenAdScreenLaunchCount]!!
        d { "screen launched $launchCount" }
        if (launchCount >= config.screenLaunchToShowAdCount) {
          d { "try to show full screen ad $launchCount" }
          if (fullScreenAds.showAd())
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

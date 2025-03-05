/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastFilter
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

@Serializable data class ScreenLaunchPrefs(val screenLaunchCount: Int = 0) {
  @Provide companion object {
    @Provide val dataStoreModule = DataStoreModule("screen_launch_prefs") { ScreenLaunchPrefs() }
  }
}

@Provide fun screenLaunchFullScreenObserver(
  adsEnabledProducer: AdsEnabledProducer,
  adFeatureRepository: AdFeatureRepository,
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAdManager: FullScreenAdManager,
  logger: Logger,
  navigator: Navigator,
  pref: DataStore<ScreenLaunchPrefs>
) = ScopeComposition<UiScope> {
  if (adsEnabledProducer.adsEnabled())
    LaunchedEffect(true) {
      navigator.launchEvents(adFeatureRepository).collectLatest {
        val launchCount = pref
          .updateData { copy(screenLaunchCount = screenLaunchCount + 1) }
          .screenLaunchCount
        logger.d { "screen launched $launchCount" }
        if (launchCount >= config.screenLaunchToShowAdCount) {
          logger.d { "try to show full screen ad $launchCount" }
          if (fullScreenAdManager.showAd())
            pref.updateData { copy(screenLaunchCount = 0) }
        }
      }
    }
}

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

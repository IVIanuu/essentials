/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.*
import arrow.core.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
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
  adsEnabledFlow: StateFlow<AdsEnabled>,
  @Inject isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAdManager: FullScreenAdManager,
  logger: Logger,
  navigator: Navigator,
  pref: DataStore<ScreenLaunchPrefs>
) = ScopeComposition<UiScope> {
  if (adsEnabledFlow.collectAsState().value.value)
    LaunchedEffect(true) {
      navigator.launchEvents().collectLatest {
        val launchCount = pref
          .updateData { copy(screenLaunchCount = screenLaunchCount + 1) }
          .screenLaunchCount
        logger.log { "screen launched $launchCount" }
        if (launchCount >= config.screenLaunchToShowAdCount) {
          logger.log { "try to show full screen ad $launchCount" }
          if (fullScreenAdManager.loadAndShowAdWithTimeout().getOrElse { false })
            pref.updateData { copy(screenLaunchCount = 0) }
        }
      }
    }
}

private fun Navigator.launchEvents(
  @Inject isAdFeatureEnabled: IsAdFeatureEnabledUseCase
): Flow<Screen<*>> {
  var lastBackStack = backStack.value
  return backStack
    .mapNotNull { currentBackStack ->
      val launchedScreens = currentBackStack
        .filter {
          it !in lastBackStack &&
              isAdFeatureEnabled(it::class, ScreenLaunchFullscreenAdFeature)
        }
      (if (currentBackStack.size > 1 && launchedScreens.isNotEmpty()) launchedScreens.first()
      else null)
        .also { lastBackStack = currentBackStack }
    }
}

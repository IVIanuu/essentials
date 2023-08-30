/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.DataStoreModule
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.getOrElse
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

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
) = ScopeWorker<UiScope> {
  adsEnabledFlow
    .flatMapLatest {
      if (!it.value) infiniteEmptyFlow()
      else navigator.launchEvents()
    }
    .collectLatest {
      val launchCount = pref
        .updateData { copy(screenLaunchCount = screenLaunchCount + 1) }
        .screenLaunchCount
      logger.log { "screen launched $launchCount" }
      if (launchCount >= config.screenLaunchToShowAdCount) {
        logger.log { "try to show full screen ad $launchCount" }
        if (fullScreenAdManager.showAdIfLoaded())
          pref.updateData { copy(screenLaunchCount = 0) }
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

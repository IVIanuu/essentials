/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide object ScreenLaunchFullscreenAdFeature : AdFeature

data class ScreenLaunchFullscreenAdConfig(val screenLaunchToShowAdCount: Int = 2) {
  companion object {
    @Provide val defaultConfig
      get() = ScreenLaunchFullscreenAdConfig()
  }
}

@Serializable data class ScreenLaunchPrefs(
  @SerialName("ad_screen_launch_count") val screenLaunchCount: Int = 0
) {
  companion object {
    @Provide val prefModule = PrefModule { ScreenLaunchPrefs() }
  }
}

@Provide fun screenLaunchFullScreenObserver(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAd: FullScreenAd,
  navigator: Navigator,
  pref: DataStore<ScreenLaunchPrefs>,
  showAds: Flow<ShowAds>,
  L: Logger
) = ScopeWorker<UiScope> {
  showAds
    .flatMapLatest {
      if (!it.value) infiniteEmptyFlow()
      else navigator.launchEvents(isFeatureEnabled)
    }
    .collectLatest {
      val launchCount = pref
        .updateData { copy(screenLaunchCount = screenLaunchCount + 1) }
        .screenLaunchCount
      log { "screen launched $launchCount" }
      if (launchCount >= config.screenLaunchToShowAdCount) {
        log { "show full screen ad $launchCount" }
        pref.updateData { copy(screenLaunchCount = 0) }
        fullScreenAd.loadAndShow()
      }
    }
}

private fun Navigator.launchEvents(isFeatureEnabled: IsAdFeatureEnabledUseCase): Flow<Unit> {
  var lastBackStack = backStack.value
  return backStack
    .mapNotNull { currentBackStack ->
      val launchedKeys = currentBackStack
        .filter {
          it !in lastBackStack &&
              isFeatureEnabled(it::class, ScreenLaunchFullscreenAdFeature)
        }
      (if (currentBackStack.size > 1 && launchedKeys.isNotEmpty()) Unit
      else null)
        .also { lastBackStack = currentBackStack }
    }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.getOrElse
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Serializable

@Provide object ScreenLaunchFullscreenAdFeature : AdFeature

data class ScreenLaunchFullscreenAdConfig(val screenLaunchToShowAdCount: Int = 4) {
  companion object {
    @Provide val defaultConfig
      get() = ScreenLaunchFullscreenAdConfig()
  }
}

@Serializable data class ScreenLaunchPrefs(val screenLaunchCount: Int = 0) {
  companion object {
    @Provide val prefModule = PrefModule { ScreenLaunchPrefs() }
  }
}

context(IsAdFeatureEnabledUseCase, Logger) @Provide fun screenLaunchFullScreenObserver(
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAd: FullScreenAd,
  navigator: Navigator,
  pref: DataStore<ScreenLaunchPrefs>,
  showAds: Flow<ShowAds>
) = ScopeWorker<UiScope> {
  showAds
    .flatMapLatest {
      if (!it.value) infiniteEmptyFlow()
      else navigator.launchEvents()
    }
    .collectLatest {
      val launchCount = pref
        .updateData { copy(screenLaunchCount = screenLaunchCount + 1) }
        .screenLaunchCount
      log { "screen launched $launchCount" }
      if (launchCount >= config.screenLaunchToShowAdCount) {
        log { "try to show full screen ad $launchCount" }
        if (fullScreenAd.loadAndShow().getOrElse { false })
          pref.updateData { copy(screenLaunchCount = 0) }
      }
    }
}

context(IsAdFeatureEnabledUseCase) private fun Navigator.launchEvents(): Flow<Unit> {
  var lastBackStack = backStack.value
  return backStack
    .mapNotNull { currentBackStack ->
      val launchedKeys = currentBackStack
        .filter {
          it !in lastBackStack &&
              isAdFeatureEnabled(it::class, ScreenLaunchFullscreenAdFeature)
        }
      (if (currentBackStack.size > 1 && launchedKeys.isNotEmpty()) Unit
      else null)
        .also { lastBackStack = currentBackStack }
    }
}

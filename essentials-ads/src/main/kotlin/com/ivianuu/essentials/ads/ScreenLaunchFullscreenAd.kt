package com.ivianuu.essentials.ads

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlin.reflect.KClass

@Provide data class ScreenLaunchFullscreenAdConfig(
  val screenLaunchToShowAdCount: Int = Int.MAX_VALUE
)

@Tag annotation class ScreenLaunchFullscreenAdBlacklist

@Provide fun screenLaunchFullScreenObserver(
  blacklist: List<@ScreenLaunchFullscreenAdBlacklist KClass<*>> = emptyList(),
  config: ScreenLaunchFullscreenAdConfig,
  fullScreenAd: FullScreenAd,
  logger: Logger,
  navigator: Navigator,
  showAds: Flow<ShowAds>
): ScopeWorker<UiComponent> = {
  var launchCount = 0
  showAds
    .flatMapLatest {
      if (!it) infiniteEmptyFlow()
      else navigator.launchEvents(blacklist)
    }
    .collectLatest {
      launchCount++
      log { "screen launched $launchCount" }
      if (launchCount >= config.screenLaunchToShowAdCount) {
        log { "show full screen ad $launchCount" }
        launchCount = 0
        fullScreenAd.loadAndShow()
      }
    }
}

private fun Navigator.launchEvents(blacklist: List<@ScreenLaunchFullscreenAdBlacklist KClass<*>>): Flow<Unit> {
  var lastBackStack = backStack.value
  return backStack
    .mapNotNull { currentBackStack ->
      val launchedKeys = currentBackStack
        .filter { it !in lastBackStack && it::class !in blacklist }
      (if (launchedKeys.isNotEmpty()) Unit
      else null)
        .also { lastBackStack = currentBackStack }
    }
}

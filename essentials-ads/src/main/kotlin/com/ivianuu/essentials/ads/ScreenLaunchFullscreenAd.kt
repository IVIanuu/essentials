package com.ivianuu.essentials.ads

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.state.asComposedFlow
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Provide object ScreenLaunchFullscreenAdFeature : AdFeature

@Provide data class ScreenLaunchFullscreenAdConfig(
  val screenLaunchToShowAdCount: Int = Int.MAX_VALUE
)

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
  showAds: State<ShowAds>,
  L: Logger
) = ScopeWorker<UiComponent> {
  showAds
    .asComposedFlow()
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
  var lastBackStack = backStack
  return snapshotFlow { backStack }
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

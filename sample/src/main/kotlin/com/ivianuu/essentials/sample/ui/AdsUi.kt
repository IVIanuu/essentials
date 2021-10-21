package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.ads.AdSize
import com.ivianuu.essentials.ads.FullScreenAd
import com.ivianuu.essentials.ads.FullScreenAdId
import com.ivianuu.essentials.ads.KeyUiAdBannerConfig
import com.ivianuu.essentials.ads.ListAdBannerConfig
import com.ivianuu.essentials.ads.ScreenLaunchFullscreenAdConfig
import com.ivianuu.essentials.ads.ShowAds
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Provide val adsHomeItem = HomeItem("Ads") { AdsKey }

object AdsKey : Key<Unit>

@Provide fun adsUi(
  fullScreenAd: FullScreenAd,
  showAdsState: MutableStateFlow<ShowAds>
): KeyUi<AdsKey> = {
  SimpleListScreen("Ads") {
    item {
      SwitchListItem(
        value = showAdsState.collectAsState().value,
        onValueChange = { showAdsState.value = it },
        title = { Text("Show ads") }
      )
    }

    item {
      val scope = rememberCoroutineScope()
      Button(onClick = { scope.launch { fullScreenAd.loadAndShow() } }) {
        Text("Show full screen ad")
      }
    }
  }
}

@Provide val keyUiAdBannerConfig = KeyUiAdBannerConfig(
  id = "ca-app-pub-3940256099942544/6300978111",
  size = AdSize.LARGE_BANNER
)
@Provide val listAdBannerConfig = ListAdBannerConfig(
  id = "ca-app-pub-3940256099942544/6300978111",
  size = AdSize.LARGE_BANNER
)
@Provide val fullScreenAdId: FullScreenAdId = "ca-app-pub-3940256099942544/1033173712"
@Provide val screenLaunchAdConfig = ScreenLaunchFullscreenAdConfig(4)

@Provide val showAdsState: @Scoped<UiComponent> MutableStateFlow<ShowAds>
  get() = MutableStateFlow(false)

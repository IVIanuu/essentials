package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.ads.AdSize
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.ads.FullScreenAd
import com.ivianuu.essentials.ads.FullScreenAdId
import com.ivianuu.essentials.ads.ListAdBannerConfig
import com.ivianuu.essentials.ads.ScreenAdBannerConfig
import com.ivianuu.essentials.ads.ScreenLaunchFullscreenAdConfig
import com.ivianuu.essentials.ads.ShowAds
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
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
        value = showAdsState.collectAsState().value.value,
        onValueChange = { showAdsState.value = ShowAds(it) },
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

@Provide fun keyUiAdBannerConfig(rp: ResourceProvider): ScreenAdBannerConfig = ScreenAdBannerConfig(
  id = loadResource(R.string.es_test_ad_unit_id_banner),
  size = AdSize.LARGE_BANNER
)
@Provide fun listAdBannerConfig(rp: ResourceProvider): ListAdBannerConfig = ListAdBannerConfig(
  id = loadResource(R.string.es_test_ad_unit_id_banner),
  size = AdSize.LARGE_BANNER
)
@Provide fun fullScreenAdId(rp: ResourceProvider): FullScreenAdId =
  FullScreenAdId(loadResource<String>(R.string.es_test_ad_unit_id_interstitial))

@Provide val screenLaunchAdConfig: ScreenLaunchFullscreenAdConfig
  get() = ScreenLaunchFullscreenAdConfig(4)

@Provide val showAdsState = MutableStateFlow(ShowAds(false))

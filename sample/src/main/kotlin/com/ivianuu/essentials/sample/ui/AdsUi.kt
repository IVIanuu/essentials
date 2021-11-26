package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch

@Provide val adsHomeItem = HomeItem("Ads") { AdsKey }

object AdsKey : Key<Unit>

@Provide fun adsUi(
  fullScreenAd: FullScreenAd,
  showAds: MutableState<ShowAds>
) = KeyUi<AdsKey> {
  SimpleListScreen("Ads") {
    item {
      SwitchListItem(
        value = showAds.value.value,
        onValueChange = { showAds.value = ShowAds(it) },
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

@Provide fun keyUiAdBannerConfig(RP: ResourceProvider) = ScreenAdBannerConfig(
  id = loadResource(R.string.es_test_ad_unit_id_banner),
  size = AdSize.LARGE_BANNER
)
@Provide fun listAdBannerConfig(RP: ResourceProvider) = ListAdBannerConfig(
    id = loadResource(R.string.es_test_ad_unit_id_banner),
    size = AdSize.LARGE_BANNER
  )

@Provide fun fullScreenAdId(RP: ResourceProvider) =
  FullScreenAdId(loadResource(R.string.es_test_ad_unit_id_interstitial))

@Provide val screenLaunchAdConfig: ScreenLaunchFullscreenAdConfig
  get() = ScreenLaunchFullscreenAdConfig(4)

@Provide val showAdsState = mutableStateOf(ShowAds(false))

/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.*
import androidx.compose.runtime.*
import com.google.android.gms.ads.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

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

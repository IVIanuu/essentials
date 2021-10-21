package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.ads.KeyUiBannerConfig
import com.ivianuu.essentials.ads.ShowAds
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val adsHomeItem = HomeItem("Ads") { AdsKey }

object AdsKey : Key<Unit>

@Provide fun adsUi(showAdsState: MutableStateFlow<ShowAds>): KeyUi<AdsKey> = {
  SimpleListScreen("Ads") {
    item {
      SwitchListItem(
        value = showAdsState.collectAsState().value,
        onValueChange = { showAdsState.value = it },
        title = { Text("Show ads") }
      )
    }
  }
}

@Provide val adBannerConfig = KeyUiBannerConfig(id = "ca-app-pub-3940256099942544/6300978111")

@Provide val showAdsState: @Scoped<UiComponent> MutableStateFlow<ShowAds>
  get() = MutableStateFlow(false)

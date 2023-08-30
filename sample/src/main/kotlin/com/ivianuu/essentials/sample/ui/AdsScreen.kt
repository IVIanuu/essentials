/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.ads.AdsEnabled
import com.ivianuu.essentials.ads.FullScreenAdConfig
import com.ivianuu.essentials.ads.FullScreenAdManager
import com.ivianuu.essentials.ads.ListAdBannerConfig
import com.ivianuu.essentials.ads.ScreenAdBannerConfig
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val fullScreenAdConfig = FullScreenAdConfig("")
@Provide val listAdBannerConfig = ListAdBannerConfig("")
@Provide val screenAdBannerConfig = ScreenAdBannerConfig("")

@Provide val adsHomeItem = HomeItem("Ads") { AdsScreen() }

class AdsScreen : Screen<Unit>

@Provide fun adsUi(
  adsEnabled: MutableStateFlow<AdsEnabled>,
  fullScreenAd: FullScreenAdManager
) = Ui<AdsScreen, Unit> {
  Scaffold(topBar = { AppBar { Text("Ads") } }) {
    VerticalList {
      item {
        SwitchListItem(
          value = adsEnabled.collectAsState().value.value,
          onValueChange = { adsEnabled.value = AdsEnabled(it) },
          title = { Text("Show ads") }
        )
      }

      item {
        Button(onClick = action { fullScreenAd.showAdIfLoaded() }) {
          Text("Show full screen ad")
        }
      }
    }
  }
}

@Provide val showAds = MutableStateFlow(AdsEnabled(false))

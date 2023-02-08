/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.ads.AdsEnabled
import com.ivianuu.essentials.ads.FullScreenAdManager
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val adsHomeItem = HomeItem("Ads") { AdsKey }

object AdsKey : Key<Unit>

@Provide fun adsUi(
  adsEnabled: MutableStateFlow<AdsEnabled>,
  fullScreenAd: FullScreenAdManager
) = SimpleKeyUi<AdsKey> {
  SimpleListScreen("Ads") {
    item {
      SwitchListItem(
        value = adsEnabled.collectAsState().value.value,
        onValueChange = { adsEnabled.value = AdsEnabled(it) },
        title = { Text("Show ads") }
      )
    }

    item {
      Button(onClick = action { fullScreenAd.loadAndShowAd() }) {
        Text("Show full screen ad")
      }
    }
  }
}

@Provide val showAds = MutableStateFlow(AdsEnabled(false))

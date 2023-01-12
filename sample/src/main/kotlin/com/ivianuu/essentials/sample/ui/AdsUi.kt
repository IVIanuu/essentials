/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.ads.FullScreenAd
import com.ivianuu.essentials.ads.ShowAds
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val adsHomeItem = HomeItem("Ads") { AdsKey }

object AdsKey : Key<Unit>

@Provide fun adsUi(fullScreenAd: FullScreenAd, showAds: MutableStateFlow<ShowAds>) = SimpleKeyUi<AdsKey> {
  SimpleListScreen("Ads") {
    item {
      SwitchListItem(
        value = showAds.collectAsState().value.value,
        onValueChange = { showAds.value = ShowAds(it) },
        title = { Text("Show ads") }
      )
    }

    item {
      Button(onClick = action { fullScreenAd.loadAndShow() }) {
        Text("Show full screen ad")
      }
    }
  }
}

@Provide val showAds = MutableStateFlow(ShowAds(false))

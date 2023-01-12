/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import com.ivianuu.essentials.ads.AdsEnabledProvider
import com.ivianuu.essentials.ads.FullScreenAdManager
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val adsHomeItem = HomeItem("Ads") { AdsKey }

object AdsKey : Key<Unit>

context(AdsEnabledProvider)
    @Provide fun adsUi(fullScreenAdManager: FullScreenAdManager) = SimpleKeyUi<AdsKey> {
  SimpleListScreen("Ads") {
    item {
      SwitchListItem(
        value = adsEnabled.bind(),
        onValueChange = { adsEnabled.cast<MutableStateFlow<Boolean>>().value = it },
        title = { Text("Show ads") }
      )
    }

    item {
      Button(onClick = action { fullScreenAdManager.loadAndShowFullScreenAd() }) {
        Text("Show full screen ad")
      }
    }
  }
}

@Provide val adsEnabledProvider = AdsEnabledProvider(MutableStateFlow(false))

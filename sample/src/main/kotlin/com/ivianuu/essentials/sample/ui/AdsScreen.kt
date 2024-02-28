/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.prefs.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide val adsHomeItem = HomeItem("Ads") { AdsScreen() }

class AdsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      adsEnabledState: MutableState<AdsEnabled>,
      fullScreenAd: FullScreenAdManager
    ) = Ui<AdsScreen> {
      ScreenScaffold(topBar = { AppBar { Text("Ads") } }) {
        VerticalList {
          item {
            SwitchListItem(
              value = adsEnabledState.value.value,
              onValueChange = { adsEnabledState.value = AdsEnabled(it) },
              title = { Text("Show ads") }
            )
          }

          item {
            Button(onClick = action { fullScreenAd.loadAndShowAdWithTimeout() }) {
              Text("Show full screen ad")
            }
          }
        }
      }
    }
  }
}

@Provide val showAds = mutableStateOf(AdsEnabled(false))
@Provide val fullScreenAdConfig = FullScreenAdConfig("")
@Provide val listAdBannerConfig = ListAdBannerConfig("")
@Provide val screenAdBannerConfig = ScreenAdBannerConfig("")

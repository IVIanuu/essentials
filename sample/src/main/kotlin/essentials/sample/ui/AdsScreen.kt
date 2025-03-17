/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.*
import essentials.ads.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.prefs.*
import injekt.*

@Provide val adsHomeItem = HomeItem("Ads") { AdsScreen() }

class AdsScreen : Screen<Unit>

@Provide @Composable fun AdsUi(
  fullScreenAd: FullScreenAds,
  scope: Scope<ScreenScope> = inject,
): Ui<AdsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Ads") } }) {
    EsLazyColumn {
      item {
        SwitchListItem(
          value = showAds,
          onValueChange = { showAds = it },
          headlineContent = { Text("Show ads") }
        )
      }

      item {
        Button(onClick = scopedAction { fullScreenAd.showAd() }) {
          Text("Show full screen ad")
        }
      }
    }
  }
}

@Provide var showAds: AdsEnabled by mutableStateOf(false)
@Provide val fullScreenAdConfig: FullScreenAdConfig = FullScreenAdConfig("")
@Provide val listAdBannerConfig: ListAdBannerConfig = ListAdBannerConfig("")
@Provide val screenAdBannerConfig: ScreenAdBannerConfig = ScreenAdBannerConfig("")

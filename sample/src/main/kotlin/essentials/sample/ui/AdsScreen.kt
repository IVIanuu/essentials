/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.ads.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Provide val adsHomeItem = HomeItem("Ads") { AdsScreen() }

class AdsScreen : Screen<Unit>

@Provide @Composable fun AdsUi(
  fullScreenAd: FullScreenAds,
  context: ScreenContext<AdsScreen> = inject,
): Ui<AdsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Ads") } }) {
    EsLazyColumn {
      item {
        SectionSwitch(
          checked = showAds,
          onCheckedChange = { showAds = it },
          title = { Text("Show ads") }
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
@Provide val appAdBannerConfig: AppAdBannerConfig = AppAdBannerConfig("")

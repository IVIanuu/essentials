/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.ads.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import injekt.*

@Provide fun adsHomeItem(
  fullScreenAd: FullScreenAds,
  ctx: ScreenContext<*> = inject
) = HomeItem("Ads") {
  BottomSheetScreen {
    Subheader { Text("Ads") }

    SectionSwitch(
      sectionType = SectionType.FIRST,
      checked = showAds,
      onCheckedChange = { showAds = it },
      title = { Text("Show ads") }
    )

    SectionListItem(
      sectionType = SectionType.LAST,
      onClick = scopedAction { fullScreenAd.showAd() },
      title = { Text("Show full screen ad") }
    )
  }
}

@Provide var showAds: AdsEnabled by mutableStateOf(false)
@Provide val fullScreenAdConfig: FullScreenAdConfig = FullScreenAdConfig("")
@Provide val listAdBannerConfig: ListAdBannerConfig = ListAdBannerConfig("")
@Provide val appAdBannerConfig: AppAdBannerConfig = AppAdBannerConfig("")

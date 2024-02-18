/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.*
import com.google.android.gms.ads.*
import com.ivianuu.injekt.*

@Immutable data class AdBannerConfig(val id: String, val size: AdSize = AdSize.LARGE_BANNER) {
  companion object {
    const val TEST_ID = "ca-app-pub-3940256099942544/6300978111"
  }
}

@Composable fun AdBanner(config: AdBannerConfig, modifier: Modifier = Modifier) {
  AndroidView(
    modifier = modifier
      .height(
        with(LocalDensity.current) {
          config.size.getHeightInPixels(LocalContext.current).toDp()
        }
      )
      .fillMaxWidth()
      .background(MaterialTheme.colors.surface),
    factory = {
      AdView(it).apply {
        adUnitId = config.id
        adSize = config.size
        loadAd(AdRequest.Builder().build())
      }
    }
  )
}

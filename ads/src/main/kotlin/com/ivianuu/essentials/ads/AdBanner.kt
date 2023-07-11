/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Immutable data class AdBannerConfig(val id: String, val size: AdSize = AdSize.LARGE_BANNER)

@Composable fun AdBanner(config: AdBannerConfig) {
  AndroidView(
    modifier = Modifier
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

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Immutable data class AdBannerConfig(val id: String, val size: AdSize)

@Composable fun AdBanner(
  modifier: Modifier = Modifier,
  config: AdBannerConfig
) {
  Box(
    modifier = modifier
      .height(
        with(LocalDensity.current) {
          config.size.getHeightInPixels(LocalContext.current).toDp()
        }
      )
      .fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    val backgroundColor = MaterialTheme.colors.surface
    AndroidView(factory = {
      AdView(it).apply {
        setBackgroundColor(backgroundColor.toArgb())
        adUnitId = config.id
        adSize = config.size
        loadAd(AdRequest.Builder().build())
      }
    })
  }
}

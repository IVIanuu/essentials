/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.*
import com.google.android.gms.ads.*

data class AdBannerConfig(val id: String, val size: AdSize)

@Composable fun AdBanner(config: AdBannerConfig) {
  Box(
    modifier = Modifier
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

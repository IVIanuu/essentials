/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.*
import com.google.android.gms.ads.*

@Immutable data class AdBannerConfig(val id: String, val size: AdSize = AdSize.LARGE_BANNER) {
  companion object {
    const val TEST_ID = "ca-app-pub-3940256099942544/6300978111"
  }
}

@Composable fun AdBanner(
  config: AdBannerConfig,
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surface
) {
  var isLoading by remember { mutableStateOf(true) }
  val alpha by animateFloatAsState(if (isLoading) 0f else 1f)
  AndroidView(
    modifier = modifier
      .height(
        with(LocalDensity.current) {
          config.size.getHeightInPixels(LocalContext.current).toDp()
        }
      )
      .fillMaxWidth()
      .background(containerColor)
      .alpha(alpha),
    factory = {
      AdView(it).apply {
        adUnitId = config.id
        setAdSize(config.size)
        adListener = object : AdListener() {
          override fun onAdLoaded() {
            super.onAdLoaded()
            isLoading = false
          }
        }
        loadAd(AdRequest.Builder().build())
      }
    }
  )
}

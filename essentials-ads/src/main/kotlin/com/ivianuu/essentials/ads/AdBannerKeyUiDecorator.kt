package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.LocalInsets
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow

typealias ShowAds = Boolean

data class KeyUiBannerConfig(
  val id: String,
  val size: AdSize = AdSize.LARGE_BANNER
)

typealias AdBannerKeyUiDecorator = KeyUiDecorator

@Provide fun adBannerKeyUiDecorators(
  config: KeyUiBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>
): AdBannerKeyUiDecorator = decorator@ { content ->
  if (config == null) {
    content()
    return@decorator
  }

  val key = LocalKeyUiComponent.current.key

  if (key is DialogKey<*>) {
    content()
    return@decorator
  }

  val showAds by showAdsFlow.collectAsState(false)

  Column {
    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (!showAds) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (showAds) {
      Surface(elevation = 8.dp) {
        val backgroundColor = MaterialTheme.colors.surface
        InsetsPadding(top = false) {
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
      }
    }
  }
}

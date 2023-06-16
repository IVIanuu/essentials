/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdSize
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ui.layout.navigationBarsPadding
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenDecorator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.StateFlow

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag {
  companion object {
    @Provide fun default(
      appConfig: AppConfig,
      resources: Resources
    ) = ScreenAdBannerConfig(
      id = resources(
        if (appConfig.isDebug) R.string.es_test_ad_unit_id_banner
        else R.string.es_screen_ad_banner_ad_unit_id
      ),
      size = AdSize.LARGE_BANNER
    )
  }
}
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

fun interface ScreenAdBanner : ScreenDecorator

@Provide fun adBannerKeyUiDecorator(
  adsEnabled: StateFlow<AdsEnabled>,
  isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenAdBannerConfig? = null,
  screen: Screen<*>
) = ScreenAdBanner decorator@{ content ->
  if (config == null) {
    content()
    return@decorator
  }

  if (!isAdFeatureEnabled(screen::class, ScreenAdBannerFeature)) {
    content()
    return@decorator
  }

  Column {
    val adsEnabled by adsEnabled.collectAsState()

    Box(
      modifier = Modifier
        .consumeWindowInsets(WindowInsets.navigationBars)
        .weight(1f)
    ) { content() }

    if (adsEnabled.value) {
      Surface(elevation = 8.dp) {
        AdBanner(
          modifier = Modifier.navigationBarsPadding(),
          config = config
        )
      }
    }
  }
}

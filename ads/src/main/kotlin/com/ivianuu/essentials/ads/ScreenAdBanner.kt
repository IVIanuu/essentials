/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag {
  @Provide companion object {
    @Provide fun final(
      adConfig: ScreenAdBannerConfig,
      appConfig: AppConfig
    ): @FinalAdConfig ScreenAdBannerConfig = if (!appConfig.isDebug) adConfig
    else adConfig.copy(id = AdBannerConfig.TEST_ID)
  }
}
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

fun interface ScreenAdBanner : ScreenDecorator

@Provide fun adBannerKeyUiDecorator(
  adsEnabledFlow: StateFlow<AdsEnabled>,
  isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: @FinalAdConfig ScreenAdBannerConfig? = null,
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
    val adsEnabled = adsEnabledFlow.collect()

    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (!adsEnabled.value) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (adsEnabled.value)
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
  }
}

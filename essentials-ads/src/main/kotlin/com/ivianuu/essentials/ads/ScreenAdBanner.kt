/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.ScreenDecorator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.StateFlow

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag {
  companion object {
    @Provide fun final(
      adConfig: ScreenAdBannerConfig,
      appConfig: AppConfig,
      resources: Resources
    ): @FinalAdConfig ScreenAdBannerConfig = if (!appConfig.isDebug) adConfig
    else adConfig.copy(id = resources(R.string.es_test_ad_unit_id_banner))
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
    val adsEnabled by adsEnabledFlow.collectAsState()

    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (!adsEnabled.value) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (adsEnabled.value) {
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
    }
  }
}

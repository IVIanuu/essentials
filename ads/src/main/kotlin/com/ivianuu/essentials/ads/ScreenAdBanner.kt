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
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object ScreenAdBannerFeature : AdFeature

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ScreenAdBannerConfigTag {
  @Provide companion object {
    @Provide fun final(
      adConfig: ScreenAdBannerConfig,
      appConfig: AppConfig
    ): @FinalAdConfig ScreenAdBannerConfig = if (!appConfig.isDebug) adConfig
    else adConfig.copy(id = AdBannerConfig.TEST_ID)
  }
}
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

@Provide class AdBannerScreenDecorator(
  private val adsEnabledState: State<AdsEnabled>,
  private val adFeatureRepository: AdFeatureRepository,
  private val config: @FinalAdConfig ScreenAdBannerConfig,
  private val screen: Screen<*>
) : ScreenDecorator {
  @Composable override fun DecoratedContent(content: @Composable () -> Unit) {
    if (!adFeatureRepository.isEnabled(screen::class, ScreenAdBannerFeature)) {
      content()
      return
    }

    Column {
      Box(modifier = Modifier.weight(1f)) {
        val currentInsets = LocalInsets.current
        CompositionLocalProvider(
          LocalInsets provides if (!adsEnabledState.value.value) currentInsets
          else currentInsets.copy(bottom = 0.dp),
          content = content
        )
      }

      if (adsEnabledState.value.value)
        Surface(elevation = 8.dp) {
          InsetsPadding(top = false) {
            AdBanner(config)
          }
        }
    }
  }
}

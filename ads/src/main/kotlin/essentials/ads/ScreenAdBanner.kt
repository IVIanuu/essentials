/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import essentials.*
import essentials.ui.navigation.*
import injekt.*

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
      Box(
        modifier = Modifier
          .weight(1f)
          .then(
            if (adsEnabledState.value.value)
              Modifier.consumeWindowInsets(WindowInsets.navigationBars)
            else Modifier
          )
      ) {
        content()
      }

      if (adsEnabledState.value.value)
        Surface(tonalElevation = 8.dp) {
          AdBanner(
            modifier = Modifier.navigationBarsPadding(),
            config = config
          )
        }
    }
  }
}

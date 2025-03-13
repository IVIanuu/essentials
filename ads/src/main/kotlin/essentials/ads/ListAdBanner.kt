/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.runtime.*
import essentials.*
import essentials.ui.common.*
import essentials.ui.navigation.*
import injekt.*

@Provide object ListAdBannerFeature : AdFeature

@Tag typealias ListAdBannerConfig = AdBannerConfig

@Provide fun finalListAdBannerConfig(
  adConfig: ListAdBannerConfig,
  appConfig: AppConfig
): @FinalAdConfig ListAdBannerConfig =
  if (!appConfig.isDebug) adConfig
  else adConfig.copy(id = AdBannerConfig.TEST_ID)

@Provide class AdBannerListDecorator(
  private val adsEnabledProducer: @Composable () -> AdsEnabled,
  private val adFeatureRepository: AdFeatureRepository,
  private val config: @FinalAdConfig ListAdBannerConfig
) : ListDecorator {
  override fun ListDecoratorScope.decoratedItems() {
    if (isVertical)
      item(null) {
        val screen = catch { LocalScope.current.screen::class }.getOrNull()
        if ((screen == null ||
              adFeatureRepository.isEnabled(screen, ListAdBannerFeature)) &&
          adsEnabledProducer())
          AdBanner(config)
      }
    content()
  }
}

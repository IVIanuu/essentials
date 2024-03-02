/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide object ListAdBannerFeature : AdFeature

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ListAdBannerConfigTag {
  @Provide companion object {
    @Provide fun final(
      adConfig: ListAdBannerConfig,
      appConfig: AppConfig
    ): @FinalAdConfig ListAdBannerConfig =
      if (!appConfig.isDebug) adConfig
      else adConfig.copy(id = AdBannerConfig.TEST_ID)
  }
}

typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

@Provide class AdBannerListDecorator(
  private val adsEnabled: State<AdsEnabled>,
  private val adFeatureRepository: AdFeatureRepository,
  private val config: @FinalAdConfig ListAdBannerConfig
) : ListDecorator {
  override fun ListDecoratorScope.decoratedItems() {
    if (isVertical)
      item(null) {
        val screen = catch { LocalScope.current.screen::class }.getOrNull()
        if ((screen == null || adFeatureRepository.isEnabled(screen, ListAdBannerFeature)) &&
          adsEnabled.value.value)
          AdBanner(config)
      }
    content()
  }
}

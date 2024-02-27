/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

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

fun interface ListAdBanner : ListDecorator

@Provide fun adBannerListDecorator(
  adsEnabledFlow: StateFlow<AdsEnabled>,
  isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: @FinalAdConfig ListAdBannerConfig? = null
) = ListAdBanner decorator@{
  if (config != null && isVertical) {
    item(null) {
      val screen = catch {
        LocalScope.current.screen::class
      }.getOrNull()
      if ((screen == null || isAdFeatureEnabled(screen, ListAdBannerFeature)) &&
        adsEnabledFlow.collect().value)
        AdBanner(config)
    }
  }

  content()
}

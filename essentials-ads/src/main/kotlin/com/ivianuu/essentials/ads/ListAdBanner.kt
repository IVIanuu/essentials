/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.getOrNull
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.screen
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.StateFlow

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag {
  companion object {
    @Provide fun final(
      adConfig: ListAdBannerConfig,
      appConfig: AppConfig,
      resources: Resources,
    ): @FinalAdConfig ListAdBannerConfig =
      if (!appConfig.isDebug) adConfig
      else adConfig.copy(id = resources(R.string.es_test_ad_unit_id_banner))
  }
}

typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

fun interface ListAdBanner : ListDecorator

@Provide fun adBannerListDecorator(
  adsEnabled: StateFlow<AdsEnabled>,
  isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: @FinalAdConfig ListAdBannerConfig? = null
) = ListAdBanner decorator@{
  if (config != null && isVertical) {
    item(null) {
      val screen = catch {
        LocalScope.current.screen::class
      }.getOrNull()
      if ((screen == null || isAdFeatureEnabled(screen, ListAdBannerFeature)) &&
        adsEnabled.collectAsState().value.value)
        AdBanner(config)
    }
  }

  content()
}

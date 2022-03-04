/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag
typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

fun interface ListAdBanner : ListDecorator

@Provide fun adBannerListDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null,
  showAdsFlow: StateFlow<ShowAds>
) = ListAdBanner decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val key = runCatching {
        LocalKeyUiElements.current<ListAdBannerComponent>().key::class
      }.getOrElse { null }
      if ((key == null || isFeatureEnabled(key, ListAdBannerFeature)) &&
        showAdsFlow.collectAsState().value.value)
        AdBanner(config)
    }
  }

  content()
}

@Provide @Element<KeyUiScope>
data class ListAdBannerComponent(val key: Key<*>)

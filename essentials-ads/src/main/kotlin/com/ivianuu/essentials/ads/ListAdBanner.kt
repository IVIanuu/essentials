/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag
typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

fun interface ListAdBanner : ListDecorator

@Provide fun adBannerListDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null,
  showAds: State<ShowAds>
) = ListAdBanner decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val key = catch {
        LocalKeyUiElements.current<ListAdBannerComponent>().key::class
      }.getOrNull()
      if ((key == null || isFeatureEnabled(key, ListAdBannerFeature)) && showAds.value.value)
        AdBanner(config)
    }
  }

  content()
}

@Provide @Element<KeyUiScope>
data class ListAdBannerComponent(val key: Key<*>)

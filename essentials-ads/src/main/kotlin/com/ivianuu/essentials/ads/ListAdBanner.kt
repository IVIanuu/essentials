/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.collectAsState
import com.github.michaelbull.result.getOrElse
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.LocalKeyUiElements
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Element
import kotlinx.coroutines.flow.StateFlow

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
      val key = catch {
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

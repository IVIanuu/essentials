/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.google.android.gms.ads.AdSize
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.compose.bind
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.ui.LocalScope
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.StateFlow

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag {
  companion object {
    @Provide fun default(
      buildInfo: BuildInfo,
      resources: Resources
    ) = ListAdBannerConfig(
      id = resources(
        if (buildInfo.isDebug) R.string.es_test_ad_unit_id_banner
        else R.string.es_list_ad_banner_ad_unit_id
      ),
      size = AdSize.LARGE_BANNER
    )
  }
}
typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

fun interface ListAdBanner : ListDecorator

@Provide fun adBannerListDecorator(
  adsEnabled: StateFlow<AdsEnabled>,
  isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null
) = ListAdBanner decorator@{
  if (config != null && isVertical) {
    item(null) {
      val key = catch {
        LocalScope.current.service<ListAdBannerComponent>().key::class
      }.getOrNull()
      if ((key == null || isAdFeatureEnabled(key, ListAdBannerFeature)) && adsEnabled.bind().value)
        AdBanner(config)
    }
  }

  content()
}

@Provide @Service<KeyUiScope>
data class ListAdBannerComponent(val key: Key<*>)

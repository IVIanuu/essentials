/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.collectAsState
import com.google.android.gms.ads.AdSize
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.LocalKeyUiElements
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Element
import kotlinx.coroutines.flow.StateFlow

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag {
  companion object {
    @Provide fun default(
      buildInfo: BuildInfo,
      RP: ResourceProvider
    ) = ListAdBannerConfig(
      id = loadResource(
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
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null,
  showAdsFlow: StateFlow<ShowAds>
) = ListAdBanner decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val key = catch {
        LocalKeyUiElements.current<ListAdBannerComponent>().key::class
      }.getOrNull()
      if ((key == null || isFeatureEnabled(key, ListAdBannerFeature)) &&
        showAdsFlow.collectAsState().value.value)
        AdBanner(config)
    }
  }

  content()
}

@Provide @Element<KeyUiScope>
data class ListAdBannerComponent(val key: Key<*>)

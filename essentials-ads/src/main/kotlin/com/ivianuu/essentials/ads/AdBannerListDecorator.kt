package com.ivianuu.essentials.ads

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.StateFlow

typealias ListAdBannerConfig = AdBannerConfig

typealias AdBannerListDecorator = ListDecorator

@Provide fun adBannerListDecorators(
  config: ListAdBannerConfig? = null,
  showAdsFlow: StateFlow<ShowAds>
): AdBannerListDecorator = decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val showAds by showAdsFlow.collectAsState()
      if (showAds) AdBanner(config)
    }
  }

  content()
}

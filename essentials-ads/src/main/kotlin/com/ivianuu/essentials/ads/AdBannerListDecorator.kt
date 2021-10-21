package com.ivianuu.essentials.ads

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow

typealias ListAdBannerConfig = AdBannerConfig

typealias AdBannerListDecorator = ListDecorator

@Provide fun adBannerListDecorators(
  config: ListAdBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>
): AdBannerListDecorator = decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val showAds by showAdsFlow.collectAsState(null)
      if (showAds == true) AdBanner(config)
    }
  }

  content()
}

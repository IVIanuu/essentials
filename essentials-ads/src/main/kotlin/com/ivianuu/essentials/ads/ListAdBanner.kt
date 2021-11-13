package com.ivianuu.essentials.ads

import androidx.compose.runtime.State
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag
typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

object ListAdBanner

@Provide fun adBannerListDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null,
  showAds: State<ShowAds>
): ListDecorator<ListAdBanner> = decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val key = catch { LocalKeyUiComponent.current.key::class }.getOrNull()
      if ((key == null || isFeatureEnabled(key, ListAdBannerFeature)) && showAds.value.value)
        AdBanner(config)
    }
  }

  content()
}

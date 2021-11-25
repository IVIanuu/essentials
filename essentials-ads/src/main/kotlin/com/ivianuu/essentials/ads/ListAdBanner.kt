package com.ivianuu.essentials.ads

import androidx.compose.runtime.State
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.ComponentElement

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
      val key = catch {
        LocalKeyUiComponent.current.element<ListAdBannerComponent>().key::class
      }.getOrNull()
      if ((key == null || isFeatureEnabled(key, ListAdBannerFeature)) && showAds.value.value)
        AdBanner(config)
    }
  }

  content()
}

@Provide @ComponentElement<KeyUiComponent>
data class ListAdBannerComponent(val key: Key<*>)

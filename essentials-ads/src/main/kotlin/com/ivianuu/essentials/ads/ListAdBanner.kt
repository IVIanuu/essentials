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

fun interface ListAdBanner : ListDecorator

@Provide fun adBannerListDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null,
  showAds: State<ShowAds>
) = ListAdBanner decorator@ {
  if (config != null && it.isVertical) {
    it.item(null) {
      val key = catch {
        LocalKeyUiComponent.current.element<ListAdBannerComponent>().key::class
      }.getOrNull()
      if ((key == null || isFeatureEnabled(key, ListAdBannerFeature)) && showAds.value.value)
        AdBanner(config)
    }
  }

  it.content()
}

@Provide @ComponentElement<KeyUiComponent>
data class ListAdBannerComponent(val key: Key<*>)

package com.ivianuu.essentials.ads

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

typealias ListAdBannerConfig = AdBannerConfig

typealias AdBannerListDecorator = ListDecorator

typealias AdBannerListDecoratorBlacklistEntry<T> = KClass<T>

@Provide fun adBannerListDecorators(
  blacklist: List<AdBannerListDecoratorBlacklistEntry<*>> = emptyList(),
  config: ListAdBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>
): AdBannerListDecorator = decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val key = catch { LocalKeyUiComponent.current.key::class }.getOrNull()
      if (key == null || key !in blacklist) {
        val showAds by showAdsFlow.collectAsState(null)
        if (showAds == true) AdBanner(config)
      }
    }
  }

  content()
}

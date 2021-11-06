package com.ivianuu.essentials.ads

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.scoped
import com.ivianuu.essentials.storage
import com.ivianuu.essentials.ui.LocalComponent
import com.ivianuu.essentials.ui.common.ListDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@Provide object ListAdBannerFeature : AdFeature

@Tag annotation class ListAdBannerConfigTag
typealias ListAdBannerConfig = @ListAdBannerConfigTag AdBannerConfig

object ListAdBanner

@Provide fun adBannerListDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ListAdBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>
): ListDecorator<ListAdBanner> = decorator@ {
  if (config != null && isVertical) {
    item(null) {
      val key = catch { LocalKeyUiComponent.current.key::class }.getOrNull()
      if (key == null || isFeatureEnabled(key, ListAdBannerFeature)) {
        var showAds by LocalComponent.current.storage.scoped { mutableStateOf<ShowAds?>(null) }
        LaunchedEffect(true) {
          showAdsFlow.collect { showAds = it }
        }
        if (showAds?.value == true) AdBanner(config)
      }
    }
  }

  content()
}

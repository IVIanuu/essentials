package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.LocalInsets
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow

@Provide object ScreenAdBannerFeature : AdFeature

typealias ScreenAdBannerConfig = AdBannerConfig

typealias ScreenAdBannerKeyUiDecorator = KeyUiDecorator

@Provide fun adBannerKeyUiDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenAdBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>
): ScreenAdBannerKeyUiDecorator = decorator@ { content ->
  if (config == null) {
    content()
    return@decorator
  }

  if (!isFeatureEnabled(LocalKeyUiComponent.current.key::class, ScreenAdBannerFeature)) {
    content()
    return@decorator
  }

  val showAds by showAdsFlow.collectAsState(null)

  Column {
    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (showAds == true) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (showAds == true) {
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
    }
  }
}

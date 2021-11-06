package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.core.InsetsPadding
import com.ivianuu.essentials.ui.core.LocalInsets
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

object ScreenAdBanner

@Provide @Scoped<KeyUiComponent> fun adBannerKeyUiDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenAdBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>,
  key: Key<*>
): KeyUiDecorator<ScreenAdBanner> {
  var showAds by mutableStateOf<ShowAds?>(null)
  return (decorator@ { content ->
    if (config == null) {
      content()
      return@decorator
    }

    if (!isFeatureEnabled(LocalKeyUiComponent.current.key::class, ScreenAdBannerFeature)) {
      content()
      return@decorator
    }

    LaunchedEffect(true) {
      showAdsFlow.collect { showAds = it }
    }

    Column {
      Box(modifier = Modifier.weight(1f)) {
        val currentInsets = LocalInsets.current
        CompositionLocalProvider(
          LocalInsets provides if (showAds?.value == true) currentInsets
          else currentInsets.copy(bottom = 0.dp),
          content = content
        )
      }

      if (showAds?.value == true) {
        Surface(elevation = 8.dp) {
          InsetsPadding(top = false) {
            AdBanner(config)
          }
        }
      }
    }
  })
}

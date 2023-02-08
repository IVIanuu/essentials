/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdSize
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.StateFlow

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag {
  companion object {
    @Provide fun default(
      buildInfo: BuildInfo,
      resources: Resources
    ) = ScreenAdBannerConfig(
      id = resources(
        if (buildInfo.isDebug) R.string.es_test_ad_unit_id_banner
        else R.string.es_screen_ad_banner_ad_unit_id
      ),
      size = AdSize.LARGE_BANNER
    )
  }
}
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

fun interface ScreenAdBanner : KeyUiDecorator

@Provide fun adBannerKeyUiDecorator(
  adsEnabled: StateFlow<AdsEnabled>,
  isAdFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenAdBannerConfig? = null,
  key: Key<*>
) = ScreenAdBanner decorator@{ content ->
  if (config == null) {
    content()
    return@decorator
  }

  if (!isAdFeatureEnabled(key::class, ScreenAdBannerFeature)) {
    content()
    return@decorator
  }

  Column {
    val adsEnabled by adsEnabled.collectAsState()

    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (!adsEnabled.value) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (adsEnabled.value) {
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
    }
  }
}

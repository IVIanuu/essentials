/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.StateFlow

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

fun interface ScreenAdBanner : KeyUiDecorator

@Provide fun adBannerKeyUiDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenAdBannerConfig? = null,
  showAdsFlow: StateFlow<ShowAds>,
  key: Key<*>
): @Scoped<KeyUiScope> ScreenAdBanner = ScreenAdBanner decorator@ { content ->
  if (config == null) {
    content()
    return@decorator
  }

  if (!isFeatureEnabled(key::class, ScreenAdBannerFeature)) {
    content()
    return@decorator
  }

  Column {
    val showAds by showAdsFlow.collectAsState()

    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (!showAds.value) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (showAds.value) {
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
    }
  }
}

/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide object ScreenAdBannerFeature : AdFeature

@Tag annotation class ScreenAdBannerConfigTag
typealias ScreenAdBannerConfig = @ScreenAdBannerConfigTag AdBannerConfig

fun interface ScreenAdBanner : KeyUiDecorator

@Provide fun adBannerKeyUiDecorator(
  isFeatureEnabled: IsAdFeatureEnabledUseCase,
  config: ScreenAdBannerConfig? = null,
  showAds: State<ShowAds>,
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
    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (showAds.value.value) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (showAds.value.value) {
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
    }
  }
}

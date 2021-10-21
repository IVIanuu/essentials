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
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.navigation.KeyUiDecorator
import com.ivianuu.essentials.ui.navigation.LocalKeyUiComponent
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.StateFlow

typealias KeyUiAdBannerConfig = AdBannerConfig

typealias AdBannerKeyUiDecorator = KeyUiDecorator

@Provide fun adBannerKeyUiDecorator(
  config: KeyUiAdBannerConfig? = null,
  showAdsFlow: StateFlow<ShowAds>
): AdBannerKeyUiDecorator = decorator@ { content ->
  if (config == null) {
    content()
    return@decorator
  }

  val key = LocalKeyUiComponent.current.key

  if (key is DialogKey<*>) {
    content()
    return@decorator
  }

  val showAds by showAdsFlow.collectAsState()

  Column {
    Box(modifier = Modifier.weight(1f)) {
      val currentInsets = LocalInsets.current
      CompositionLocalProvider(
        LocalInsets provides if (!showAds) currentInsets
        else currentInsets.copy(bottom = 0.dp),
        content = content
      )
    }

    if (showAds) {
      Surface(elevation = 8.dp) {
        InsetsPadding(top = false) {
          AdBanner(config)
        }
      }
    }
  }
}

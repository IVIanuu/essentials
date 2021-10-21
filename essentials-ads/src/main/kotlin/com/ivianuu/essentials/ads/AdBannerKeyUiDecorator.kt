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
import com.ivianuu.injekt.Spread
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

typealias KeyUiAdBannerConfig = AdBannerConfig

typealias AdBannerKeyUiDecorator = KeyUiDecorator

typealias AdBannerKeyUiBlacklistEntry<T> = KClass<T>

@Provide fun <@Spread T : DialogKey<*>> dialogAdBannerKeyUiBlacklistEntry(
  clazz: KClass<T>
): AdBannerKeyUiBlacklistEntry<T> = clazz

@Provide fun adBannerKeyUiDecorator(
  keyBlacklist: List<AdBannerKeyUiBlacklistEntry<*>>,
  config: KeyUiAdBannerConfig? = null,
  showAdsFlow: Flow<ShowAds>
): AdBannerKeyUiDecorator = decorator@ { content ->
  if (config == null) {
    content()
    return@decorator
  }

  if (LocalKeyUiComponent.current.key::class in keyBlacklist) {
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

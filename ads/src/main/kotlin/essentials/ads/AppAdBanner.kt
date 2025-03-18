/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import essentials.*
import essentials.ui.app.*
import essentials.ui.navigation.*
import injekt.*

@Provide object AppAdBannerFeature : AdFeature {
  @Provide fun loadingOrder(): LoadingOrder<AppAdBannerFeature> =
    LoadingOrder<AppAdBannerFeature>().after<AppTheme>()
}

@Tag typealias AppAdBannerConfig = AdBannerConfig

@Provide fun finalAppAdBannerConfig(
  adConfig: AppAdBannerConfig,
  appConfig: AppConfig
): @FinalAdConfig AppAdBannerConfig = if (!appConfig.isDebug) adConfig
else adConfig.copy(id = AdBannerConfig.TEST_ID)

@Provide @Composable fun AppAdBannerAppUiDecoration(
  adsEnabled: AdsEnabled,
  adFeatureRepository: AdFeatureRepository,
  config: @FinalAdConfig AppAdBannerConfig,
  navigator: Navigator,
  content: @Composable () -> Unit
): AppUiDecoration<AppAdBannerFeature> {
  val currentAdsEnabled by rememberUpdatedState(adsEnabled)

  Column {
    Box(
      modifier = Modifier
        .weight(1f)
        .then(
          if (currentAdsEnabled)
            Modifier.consumeWindowInsets(WindowInsets.navigationBars)
          else Modifier
        )
    ) {
      content()
    }

    val showBanner by remember {
      derivedStateOf {
        currentAdsEnabled && navigator.backStack
          .lastOrNull { it !is OverlayScreen<*> }
          ?.let { adFeatureRepository.isEnabled(it::class, AppAdBannerFeature) } == true
      }
    }

    Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
      AnimatedVisibility(
        showBanner,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        AdBanner(
          modifier = Modifier.navigationBarsPadding(),
          config = config,
          containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
      }
    }
  }
}

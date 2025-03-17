/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ads

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.ui.navigation.*
import injekt.*
import kotlin.reflect.*

interface AdFeature

@Tag typealias AdFeatures<T> = List<AdFeature>

@Provide object AdFeatureProviders {
  @Provide fun <T : Screen<*>> default(allFeatures: List<AdFeature>): AdFeatures<T> =
    allFeatures

  @Provide fun <T : RootScreen> root(allFeatures: List<AdFeature>): AdFeatures<T> =
    allFeatures.fastFilter { it != ListAdBannerFeature }

  @Provide fun <T : OverlayScreen<*>> overlay(): AdFeatures<T> = emptyList()

  @Provide fun <T : CriticalUserFlowScreen<*>> criticalUserFlow(): AdFeatures<T> =
    listOf(ScreenAdBannerFeature)

  @Provide fun <@AddOn T : Ui<S>, S : Screen<*>> binding(
    keyClass: KClass<S>,
    features: AdFeatures<S>
  ): Pair<KClass<out Screen<*>>, AdFeatures<*>> = keyClass to features
}

@Stable @Provide class AdFeatureRepository(
  private val featuresByScreen: Map<KClass<out Screen<*>>, AdFeatures<*>>
) {
  fun isEnabled(screenClass: KClass<out Screen<*>>, feature: AdFeature): Boolean =
    featuresByScreen[screenClass]?.contains(feature) == true
}

@Tag annotation class FinalAdConfig

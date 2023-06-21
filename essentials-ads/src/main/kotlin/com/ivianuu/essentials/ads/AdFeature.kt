/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.ui.navigation.CriticalUserFlowScreen
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.RootScreen
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

interface AdFeature

@JvmInline value class AdFeatures<T : Screen<*>>(val value: List<AdFeature>) {
  companion object {
    @Provide fun <T : Screen<*>> defaultAdFeatures(allFeatures: List<AdFeature>): AdFeatures<T> =
      AdFeatures(allFeatures)

    @Provide fun <T : RootScreen> defaultRootKeyAdFeatures(allFeatures: List<AdFeature>): AdFeatures<T> =
      AdFeatures(allFeatures.filter { it != ListAdBannerFeature })

    @Provide fun <T : OverlayScreen<*>> defaultOverlayAdFeatures(): AdFeatures<T> =
      AdFeatures(emptyList())

    @Provide fun <T : CriticalUserFlowScreen<*>> defaultCriticalUserFlowAdFeatures(): AdFeatures<T> =
      AdFeatures(listOf(ScreenAdBannerFeature))

    @Provide fun <@Spread T : Ui<S, *>, S : Screen<*>> adFeatureConfigMapEntry(
      keyClass: KClass<S>,
      features: AdFeatures<S>
    ): Pair<KClass<out Screen<*>>, AdFeatures<*>> = keyClass to features
  }
}

fun interface IsAdFeatureEnabledUseCase {
  operator fun invoke(screenClass: KClass<out Screen<*>>, feature: AdFeature): Boolean
}

@Provide fun isAdFeatureEnabledUseCase(
  featuresByScreen: Map<KClass<out Screen<*>>, AdFeatures<*>>
) = IsAdFeatureEnabledUseCase { keyClass, feature ->
  featuresByScreen[keyClass]?.value?.contains(feature) == true
}

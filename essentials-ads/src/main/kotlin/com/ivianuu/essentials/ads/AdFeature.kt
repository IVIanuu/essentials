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

@JvmInline value class AdFeatures<K : Screen<*>>(val value: List<AdFeature>) {
  companion object {
    @Provide fun <K : Screen<*>> defaultAdFeatures(allFeatures: List<AdFeature>): AdFeatures<K> =
      AdFeatures(allFeatures)

    @Provide fun <K : RootScreen> defaultRootKeyAdFeatures(allFeatures: List<AdFeature>): AdFeatures<K> =
      AdFeatures(allFeatures.filter { it != ListAdBannerFeature })

    @Provide fun <K : OverlayScreen<*>> defaultPopupAdFeatures(): AdFeatures<K> =
      AdFeatures(emptyList())

    @Provide fun <K : CriticalUserFlowScreen<*>> defaultCriticalUserFlowAdFeatures(): AdFeatures<K> =
      AdFeatures(listOf(ScreenAdBannerFeature))

    @Provide fun <@Spread T : Ui<K, *>, K : Screen<*>> adFeatureConfigMapEntry(
      keyClass: KClass<K>,
      features: AdFeatures<K>
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

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.ui.navigation.CriticalUserFlowKey
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.RootKey
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

interface AdFeature

@JvmInline value class AdFeatures<K : Key<*>>(val value: List<AdFeature>) {
  companion object {
    @Provide fun <K : Key<*>> defaultAdFeatures(allFeatures: List<AdFeature>): AdFeatures<K> =
      AdFeatures(allFeatures)

    @Provide fun <K : RootKey> defaultRootKeyAdFeatures(allFeatures: List<AdFeature>): AdFeatures<K> =
      AdFeatures(allFeatures.filter { it != ListAdBannerFeature })

    @Provide fun <K : PopupKey<*>> defaultPopupAdFeatures(): AdFeatures<K> = AdFeatures(emptyList())

    @Provide fun <K : CriticalUserFlowKey<*>> defaultCriticalUserFlowAdFeatures(): AdFeatures<K> =
      AdFeatures(listOf(ScreenAdBannerFeature))

    @Provide fun <@Spread T : KeyUi<K, *>, K : Key<*>> adFeatureConfigMapEntry(
      keyClass: KClass<K>,
      features: AdFeatures<K>
    ): Pair<KClass<out Key<*>>, AdFeatures<*>> = keyClass to features
  }
}

fun interface IsAdFeatureEnabledUseCase {
  operator fun invoke(keyClass: KClass<out Key<*>>, feature: AdFeature): Boolean
}

@Provide fun isAdFeatureEnabledUseCase(
  featuresByKey: Map<KClass<out Key<*>>, AdFeatures<*>>
) = IsAdFeatureEnabledUseCase { keyClass, feature ->
  featuresByKey[keyClass]?.value?.contains(feature) == true
}

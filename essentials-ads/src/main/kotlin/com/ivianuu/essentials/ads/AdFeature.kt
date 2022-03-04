/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

interface AdFeature

@JvmInline value class AdFeatures<K : Key<*>>(val value: List<AdFeature>)

@Provide fun <K : Key<*>> defaultAdFeatures(allFeatures: List<AdFeature>): AdFeatures<K> =
  AdFeatures(allFeatures)

@Provide fun <K : PopupKey<*>> defaultPopupAdFeatures(): AdFeatures<K> = AdFeatures(emptyList())

@Provide fun <@Spread T : KeyUi<K>, K : Key<*>> adFeatureConfigMapEntry(
  keyClass: KClass<K>,
  features: AdFeatures<K>
): Pair<KClass<out Key<*>>, AdFeatures<*>> = (keyClass to features) as Pair<KClass<out Key<*>>, AdFeatures<*>>

fun interface IsAdFeatureEnabledUseCase : (KClass<out Key<*>>, AdFeature) -> Boolean

@Provide fun isAdFeatureEnabledUseCase(
  featuresByKey: Map<KClass<out Key<*>>, AdFeatures<*>>
) = IsAdFeatureEnabledUseCase { keyClass, feature ->
  featuresByKey[keyClass]?.value?.contains(feature) == true
}

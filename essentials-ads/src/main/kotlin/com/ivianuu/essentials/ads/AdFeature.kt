/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ads

import com.ivianuu.essentials.cast
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

interface AdFeature

@JvmInline value class AdFeatures<K>(val value: List<AdFeature>)

@Provide fun <K : Key<*>> defaultAdFeatures(allFeatures: List<AdFeature>): AdFeatures<K> =
  AdFeatures(allFeatures)

@Provide fun <K : DialogKey<*>> defaultDialogAdFeatures(): AdFeatures<K> = AdFeatures(emptyList())

@Provide fun <@Spread T : KeyUi<K>, K : Key<*>> adFeatureConfigMapEntry(
  keyClass: KClass<K>,
  features: AdFeatures<K>
): Pair<KClass<out Key<*>>, AdFeatures<*>> = (keyClass to features).cast()

fun interface IsAdFeatureEnabledUseCase : (KClass<out Key<*>>, AdFeature) -> Boolean

@Provide fun isAdFeatureEnabledUseCase(
  featuresByKey: Map<KClass<out Key<*>>, AdFeatures<*>>
) = IsAdFeatureEnabledUseCase { keyClass, feature ->
  featuresByKey[keyClass]?.value?.contains(feature) == true
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ads

import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

interface AdFeature

@JvmInline value class AdFeatures<T : Screen<*>>(val value: List<AdFeature>) {
  @Provide companion object {
    @Provide fun <T : Screen<*>> defaultAdFeatures(allFeatures: List<AdFeature>): AdFeatures<T> =
      AdFeatures(allFeatures)

    @Provide fun <T : RootScreen> defaultRootAdFeatures(allFeatures: List<AdFeature>): AdFeatures<T> =
      AdFeatures(allFeatures.filter { it != ListAdBannerFeature })

    @Provide fun <T : OverlayScreen<*>> defaultOverlayAdFeatures(): AdFeatures<T> =
      AdFeatures(emptyList())

    @Provide fun <T : CriticalUserFlowScreen<*>> defaultCriticalUserFlowAdFeatures(): AdFeatures<T> =
      AdFeatures(listOf(ScreenAdBannerFeature))

    @Provide fun <@AddOn T : Ui<S>, S : Screen<*>> adFeatureConfigBinding(
      keyClass: KClass<S>,
      features: AdFeatures<S>
    ): Pair<KClass<out Screen<*>>, AdFeatures<*>> = keyClass to features
  }
}

@Stable @Provide class AdFeatureRepository(
  private val featuresByScreen: Map<KClass<out Screen<*>>, AdFeatures<*>>
) {
  fun isEnabled(screenClass: KClass<out Screen<*>>, feature: AdFeature): Boolean =
    featuresByScreen[screenClass]?.value?.contains(feature) == true
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class FinalAdConfig

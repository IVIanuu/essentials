/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.LoadingOrder
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.app.IsFirstRun
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.UserflowBuilder
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

@JvmInline value class AppStartPremiumHintEnabled(val value: Boolean) {
  @Provide companion object {
    @Provide val default get() = AppStartPremiumHintEnabled(true)
  }
}

@Provide class PremiumHintUserflowBuilder(
  private val enabled: AppStartPremiumHintEnabled,
  private val isFirstRun: suspend () -> IsFirstRun,
  private val premiumVersionManager: PremiumVersionManager
) : UserflowBuilder {
  private var hintShown = false

  override suspend fun invoke(): List<Screen<*>> {
    if (hintShown || !enabled.value || premiumVersionManager.isPremiumVersion.first())
    hintShown = true
    return listOf(
      GoPremiumScreen(
        showTryBasicOption = isFirstRun().value,
        allowBackNavigation = !isFirstRun().value
      )
    )
  }

  companion object {
    @Provide val loadingOrder: LoadingOrder<PremiumHintUserflowBuilder>
      get() = LoadingOrder<PremiumHintUserflowBuilder>()
        .last()
  }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.ivianuu.essentials.app.IsFirstRun
import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.ui.navigation.UserflowBuilder
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@JvmInline value class AppStartPremiumHintEnabled(val value: Boolean) {
  companion object {
    @Provide val default get() = AppStartPremiumHintEnabled(true)
  }
}

fun interface PremiumHintUserflowBuilder : UserflowBuilder {
  companion object {
    @Provide val loadingOrder: LoadingOrder<PremiumHintUserflowBuilder>
      get() = LoadingOrder<PremiumHintUserflowBuilder>()
        .last()
  }
}

@Provide fun premiumHintUserflowBuilder(
  enabled: AppStartPremiumHintEnabled,
  isFirstRunFlow: Flow<IsFirstRun>,
  premiumVersionManager: PremiumVersionManager
): PremiumHintUserflowBuilder {
  var hintShown = false
  return PremiumHintUserflowBuilder {
    if (hintShown || !enabled.value || premiumVersionManager.isPremiumVersion.first())
      return@PremiumHintUserflowBuilder emptyList()

    hintShown = true
    val isFirstRun = isFirstRunFlow.first().value

    listOf(
      GoPremiumScreen(
        showTryBasicOption = isFirstRun,
        allowBackNavigation = !isFirstRun
      )
    )
  }
}

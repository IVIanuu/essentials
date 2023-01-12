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
    @Provide val loadingOrder = LoadingOrder<PremiumHintUserflowBuilder>()
      .last()
  }
}

context(PremiumVersionManager) @Provide fun premiumHintUserflowBuilder(
  enabled: AppStartPremiumHintEnabled,
  isFirstRunFlow: Flow<IsFirstRun>
) = PremiumHintUserflowBuilder {
  if (!enabled.value || isPremiumVersion.first()) return@PremiumHintUserflowBuilder emptyList()

  val isFirstRun = isFirstRunFlow.first().value

  listOf(
    GoPremiumKey(
      showTryBasicOption = isFirstRun,
      allowBackNavigation = !isFirstRun
    )
  )
}

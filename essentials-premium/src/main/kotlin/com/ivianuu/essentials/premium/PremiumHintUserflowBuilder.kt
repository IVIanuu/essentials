/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.ivianuu.essentials.app.LoadingOrder
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.ui.navigation.UserflowBuilder
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

@JvmInline value class AppStartPremiumHintEnabled(val value: Boolean) {
  companion object {
    @Provide val default: AppStartPremiumHintEnabled
      get() = AppStartPremiumHintEnabled(true)
  }
}

fun interface PremiumHintUserflowBuilder : UserflowBuilder {
  companion object {
    @Provide val loadingOrder = LoadingOrder<PremiumHintUserflowBuilder>()
      .last()
  }
}

@Provide fun premiumHintUserflowBuilder(
  enabled: AppStartPremiumHintEnabled,
  premiumVersionManager: PremiumVersionManager,
  pref: DataStore<PremiumPrefs>
) = PremiumHintUserflowBuilder {
  if (!enabled.value ||
    premiumVersionManager.isPremiumVersion.first()) return@PremiumHintUserflowBuilder emptyList()

  val firstAppStart = pref.data.first().firstStart

  pref.updateData { copy(firstStart = false) }

  listOf(
    GoPremiumKey(
      showTryBasicOption = firstAppStart,
      allowBackNavigation = !firstAppStart
    )
  )
}

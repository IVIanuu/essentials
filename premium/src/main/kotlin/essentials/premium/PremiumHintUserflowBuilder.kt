/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.premium

import essentials.*
import essentials.app.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide class PremiumHintUserflowBuilder(
  private val isFirstRun: suspend () -> IsFirstRun,
  private val premiumVersionManager: PremiumVersionManager
) : UserflowBuilder {
  override suspend fun createUserflow(): List<Screen<*>> =
    if (hintShown || premiumVersionManager.isPremiumVersion.first()) emptyList()
    else listOf(
      GoPremiumScreen(
        showTryBasicOption = isFirstRun().value,
        allowBackNavigation = !isFirstRun().value
      )
    ).also { hintShown = true }

  companion object {
    private var hintShown = false

    @Provide val loadingOrder: LoadingOrder<PremiumHintUserflowBuilder>
      get() = LoadingOrder<PremiumHintUserflowBuilder>()
        .last()
  }
}

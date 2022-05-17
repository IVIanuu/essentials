/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@JvmInline value class AppStartPremiumHintEnabled(val value: Boolean) {
  companion object {
    @Provide val default: AppStartPremiumHintEnabled
      get() = AppStartPremiumHintEnabled(true)
  }
}

@Provide fun appStartPremiumHintWorker(
  enabled: AppStartPremiumHintEnabled,
  navigator: Navigator,
  premiumVersionManager: PremiumVersionManager,
  pref: DataStore<AppStartPremiumHintPrefs>
) = ScopeWorker<UiScope> {
  if (!enabled.value ||
    premiumVersionManager.isPremiumVersion.first()) return@ScopeWorker

  val firstAppStart = pref.data.first().firstAppStart

  navigator.push(
    GoPremiumKey(
      showTryBasicOption = firstAppStart,
      allowBackNavigation = !firstAppStart,
      showAdOnBackNavigation = true
    )
  )

  pref.updateData { copy(firstAppStart = false) }
}

@Serializable data class AppStartPremiumHintPrefs(
  val firstAppStart: Boolean = true
) {
  companion object {
    @Provide val prefModule = PrefModule { AppStartPremiumHintPrefs() }
  }
}

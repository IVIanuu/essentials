/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.premium

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import essentials.*
import essentials.app.*
import essentials.data.DataStore
import essentials.data.edit
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide class PremiumHintUserflowBuilder(
  private val preferencesStore: DataStore<Preferences>,
  private val premiumVersionManager: PremiumVersionManager
) : UserflowBuilder {
  override suspend fun createUserflow(): List<Screen<*>> {
    val hintShown = preferencesStore.data.first()[HintShownKey] == true
    return if (hintShown || premiumVersionManager.isPremiumVersion.first()) emptyList()
    else listOf(GoPremiumScreen(showTryBasicOption = true, allowBackNavigation = false))
      .also { preferencesStore.edit { this[HintShownKey] = true } }
  }

  companion object {
    @Provide val loadingOrder: LoadingOrder<PremiumHintUserflowBuilder>
      get() = LoadingOrder<PremiumHintUserflowBuilder>()
        .last()
  }
}

private val HintShownKey = booleanPreferencesKey("hint_shown")

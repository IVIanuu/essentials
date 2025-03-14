/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.premium

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import essentials.*
import essentials.ads.*
import essentials.billing.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.data.preferencesDataStore
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Tag typealias IsPremiumVersion = Boolean?

@Provide @Composable fun isPremiumVersion(
  billingManager: BillingManager,
  premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>
): @ComposeIn<AppScope> IsPremiumVersion {
  val premiumVersionStates = remember { oldPremiumVersionSkus + premiumVersionSku }
    .fastMap { billingManager.isPurchased(it) }
  return if (premiumVersionStates.fastAll { it == null }) null
  else premiumVersionStates.fastAny { it == true }
}

interface Paywall {
  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R?
}

@Provide fun paywall(
  isPremiumVersion: @Composable () -> IsPremiumVersion,
  scope: Scope<*> = inject
): Paywall = object : Paywall {
  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (moleculeFlow { isPremiumVersion() }.filterNotNull().first()) return block()

    coroutineScope().launch {
      showToast("This functionality is only available in the premium version!")
      if (!unlockScreen()) return@launch
      navigator(launchUi()).push(GoPremiumScreen(showTryBasicOption = false))
    }

    return null
  }
}

@Tag typealias PremiumVersionSku = Sku
@Provide val defaultPremiumVersionSku: PremiumVersionSku
  get() = Sku("premium_version", Sku.Type.SUBS)

@Tag typealias OldPremiumVersionSku = Sku

@Provide val defaultOldPermissionVersionSkus
  get() = emptyList<OldPremiumVersionSku>()

@Provide @Composable fun premiumAdsEnabled(
  isPremiumVersion: IsPremiumVersion
): AdsEnabled = isPremiumVersion != true

@Provide class PremiumHintUserflowBuilder(
  private val isPremiumVersion: @Composable () -> IsPremiumVersion,
  @property:Provide private val scope: Scope<*> = inject
) : UserflowBuilder {
  override suspend fun createUserflow(): List<Screen<*>> {
    val hintShown = preferencesDataStore().data.first()[HintShownKey] == true
    return if (hintShown || moleculeFlow { isPremiumVersion() }.filterNotNull().first()) emptyList()
    else listOf(GoPremiumScreen(showTryBasicOption = true, allowBackNavigation = false))
      .also { preferencesDataStore().edit { it[HintShownKey] = true } }
  }

  companion object {
    @Provide val loadingOrder: LoadingOrder<PremiumHintUserflowBuilder>
      get() = LoadingOrder<PremiumHintUserflowBuilder>()
        .last()
  }
}

private val HintShownKey = booleanPreferencesKey("hint_shown")

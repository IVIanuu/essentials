/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.premium

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastMap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import arrow.fx.coroutines.*
import essentials.*
import essentials.ads.*
import essentials.billing.*
import essentials.compose.moleculeFlow
import essentials.coroutines.*
import essentials.logging.*
import essentials.ui.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import injekt.Tag
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.collections.plus

@Tag typealias IsPremiumVersion = Boolean?

@Provide @Composable fun isPremiumVersion(
  billingManager: BillingManager,
  premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>
): @ComposeIn<AppScope> IsPremiumVersion {
  val premiumVersionStates = (oldPremiumVersionSkus + premiumVersionSku)
    .fastMap {
      remember { billingManager.isPurchased(it) }
        .collectAsState(nullOf()).value
    }

  return if (premiumVersionStates.fastAll { it == null }) null
  else premiumVersionStates.any { it == true }
}

interface Paywall {
  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R?
}

@Provide fun paywall(
  appUiStarter: AppUiStarter,
  deviceScreenManager: DeviceScreenManager,
  isPremiumVersion: @Composable () -> IsPremiumVersion,
  scope: ScopedCoroutineScope<AppScope>,
  showToast: showToast
): Paywall = object : Paywall {
  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (moleculeFlow { isPremiumVersion() }.filterNotNull().first()) return block()

    scope.launch {
      showToast("This functionality is only available in the premium version!")
      if (!deviceScreenManager.unlockScreen()) return@launch
      appUiStarter.startAppUi()
        .cast<UiScopeOwner>()
        .uiScope
        .navigator
        .push(GoPremiumScreen(showTryBasicOption = false))
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

@Provide @Composable fun PremiumVersionDowngradeManager(
  downgradeHandlers: () -> List<suspend () -> PremiumDowngradeResult>,
  isPremiumVersion: IsPremiumVersion,
  logger: Logger,
  preferencesStore: DataStore<Preferences>
): ScopeCompositionResult<AppScope> {
  if (isPremiumVersion != null)
    LaunchedEffect(isPremiumVersion) {
      if (!isPremiumVersion && preferencesStore.data.first()[WasPremiumVersionKey] == true) {
        logger.d { "handle premium version downgrade" }
        downgradeHandlers().parMap { it() }
      }
      preferencesStore.edit { it[WasPremiumVersionKey] = isPremiumVersion }
    }
}

private val WasPremiumVersionKey = booleanPreferencesKey("was_premium_version")

@Tag typealias PremiumDowngradeResult = Unit

@Provide @Composable fun premiumAdsEnabled(
  isPremiumVersion: IsPremiumVersion
): AdsEnabled = isPremiumVersion != true

@Provide val defaultPremiumDowngradeHandlers
  get() = emptyList<suspend () -> PremiumDowngradeResult>()

@Provide class PremiumHintUserflowBuilder(
  private val isPremiumVersion: @Composable () -> IsPremiumVersion,
  private val preferencesStore: DataStore<Preferences>
) : UserflowBuilder {
  override suspend fun createUserflow(): List<Screen<*>> {
    val hintShown = preferencesStore.data.first()[HintShownKey] == true
    return if (hintShown || moleculeFlow { isPremiumVersion() }.filterNotNull().first()) emptyList()
    else listOf(GoPremiumScreen(showTryBasicOption = true, allowBackNavigation = false))
      .also { preferencesStore.edit { it[HintShownKey] = true } }
  }

  companion object {
    @Provide val loadingOrder: LoadingOrder<PremiumHintUserflowBuilder>
      get() = LoadingOrder<PremiumHintUserflowBuilder>()
        .last()
  }
}

private val HintShownKey = booleanPreferencesKey("hint_shown")

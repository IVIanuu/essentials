/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.premium

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastMap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import arrow.fx.coroutines.*
import com.android.billingclient.api.*
import essentials.*
import essentials.ads.*
import essentials.billing.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.logging.*
import essentials.ui.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import injekt.Tag
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface PremiumVersionManager {
  val isPremiumVersion: Flow<Boolean>

  suspend fun getPremiumSkuDetails(): SkuDetails

  suspend fun purchasePremiumVersion(): Boolean

  suspend fun consumePremiumVersion(): Boolean

  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R?
}

@Provide @Eager<AppScope> class PremiumVersionManagerImpl(
  private val appUiStarter: AppUiStarter,
  private val billingManager: BillingManager,
  private val deviceScreenManager: DeviceScreenManager,
  private val downgradeHandlers: () -> List<suspend () -> PremiumDowngradeResult>,
  private val logger: Logger,
  private val preferencesStore: DataStore<Preferences>,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : PremiumVersionManager {
  override val isPremiumVersion = moleculeFlow {
    val isPremiumVersion = (oldPremiumVersionSkus + premiumVersionSku)
      .fastMap {
        produceState(nullOf()) {
          billingManager.isPurchased(it).collect { value = it }
        }.value == true
      }
      .fastAny { it }

    LaunchedEffect(isPremiumVersion) {
      if (!isPremiumVersion && preferencesStore.data.first()[WasPremiumVersionKey] == true) {
        logger.d { "handle premium version downgrade" }
        downgradeHandlers().parMap { it() }
      }
      preferencesStore.edit { it[WasPremiumVersionKey] = isPremiumVersion }
    }

    isPremiumVersion
  }
    .filterNotNull()
    .shareIn(scope, SharingStarted.Eagerly, 1)

  override suspend fun getPremiumSkuDetails(): SkuDetails =
    billingManager.getSkuDetails(premiumVersionSku)!!

  override suspend fun purchasePremiumVersion() =
    billingManager.purchase(premiumVersionSku, true, true)

  override suspend fun consumePremiumVersion() = billingManager.consumePurchase(premiumVersionSku)

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    scope.launch {
      toaster.toast("This functionality is only available in the premium version!")
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

@Tag typealias OldPremiumVersionSku = Sku

@Provide object PremiumVersionSkuProviders {
  @Provide val defaultPremiumVersionSku: PremiumVersionSku
    get() = Sku("premium_version", Sku.Type.SUBS)
  @Provide val defaultOldPermissionVersionSkus
    get() = emptyList<OldPremiumVersionSku>()
}

@Provide @Composable fun premiumAdsEnabled(
  premiumVersionManager: PremiumVersionManager
): AdsEnabled = produceState(false) {
  premiumVersionManager.isPremiumVersion.collect { value = !it }
}.value

private val WasPremiumVersionKey = booleanPreferencesKey("was_premium_version")

@Tag typealias PremiumDowngradeResult = Unit

@Provide val defaultPremiumDowngradeHandlers
  get() = emptyList<suspend () -> PremiumDowngradeResult>()

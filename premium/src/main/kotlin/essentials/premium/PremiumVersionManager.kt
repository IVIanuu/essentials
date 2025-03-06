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
import essentials.data.*
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
  private val downgradeHandlers: () -> List<PremiumDowngradeHandler>,
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
        downgradeHandlers().parMap { it.onPremiumDowngrade() }
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

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class PremiumVersionSkuTag {
  @Provide companion object {
    @Provide val default: PremiumVersionSku get() = Sku("premium_version", Sku.Type.SUBS)
  }
}
typealias PremiumVersionSku = @PremiumVersionSkuTag Sku

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class OldPremiumVersionSkuTag {
  @Provide companion object {
    @Provide val defaultOldPermissionVersionSkus get() = emptyList<OldPremiumVersionSku>()
  }
}
typealias OldPremiumVersionSku = @OldPremiumVersionSkuTag Sku

@Provide fun premiumAdsEnabledProducer(
  premiumVersionManager: PremiumVersionManager
) = AdsEnabledProducer {
  produceState(false) {
    premiumVersionManager.isPremiumVersion.collect { value = !it }
  }.value
}

private val WasPremiumVersionKey = booleanPreferencesKey("was_premium_version")

fun interface PremiumDowngradeHandler {
  suspend fun onPremiumDowngrade()

  @Provide companion object {
    @Provide val defaultHandlers get() = emptyList<PremiumDowngradeHandler>()
  }
}

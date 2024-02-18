/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import arrow.fx.coroutines.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide @Eager<AppScope> class PremiumVersionManager(
  private val appUiStarter: AppUiStarter,
  private val billingService: BillingService,
  private val deviceScreenManager: DeviceScreenManager,
  private val downgradeHandlers: () -> List<PremiumDowngradeHandler>,
  private val logger: Logger,
  private val pref: DataStore<PremiumVersionPrefs>,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) {
  val premiumSkuDetails: Flow<SkuDetails> =
    flow { emit(billingService.getSkuDetails(premiumVersionSku)!!) }

  val isPremiumVersion: Flow<Boolean> = combine(
    billingService.isPurchased(premiumVersionSku),
    if (oldPremiumVersionSkus.isNotEmpty())
      combine(oldPremiumVersionSkus.map { billingService.isPurchased(it) }) {
        it.any { it }
      }
    else flowOf(false)
  ) { a, b -> a || b }
    .onEach { isPremiumVersion ->
      scope.launch {
        if (!isPremiumVersion && pref.data.first().wasPremiumVersion) {
          logger.log { "handle premium version downgrade" }
          downgradeHandlers().parMap { it() }
        }
        pref.updateData {
          copy(wasPremiumVersion = isPremiumVersion)
        }
      }
    }
    .shareIn(scope, SharingStarted.Eagerly, 1)

  suspend fun purchasePremiumVersion() =
    billingService.purchase(premiumVersionSku, true, true)

  suspend fun consumePremiumVersion() = billingService.consumePurchase(premiumVersionSku)

  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    scope.launch {
      toaster("This functionality is only available in the premium version!")
      if (!deviceScreenManager.unlockScreen()) return@launch
      appUiStarter()
        .cast<UiScopeOwner>()
        .uiScope
        .navigator
        .push(GoPremiumScreen(showTryBasicOption = false))
    }

    return null
  }
}

@Tag annotation class PremiumVersionSkuTag {
  @Provide companion object {
    @Provide val default: PremiumVersionSku get() = Sku("premium_version", Sku.Type.SUBS)
  }
}
typealias PremiumVersionSku = @PremiumVersionSkuTag Sku

@Tag annotation class OldPremiumVersionSkuTag {
  @Provide companion object {
    @Provide val defaultOldPermissionVersionSkus get() = emptyList<OldPremiumVersionSku>()
  }
}
typealias OldPremiumVersionSku = @OldPremiumVersionSkuTag Sku

@Provide fun premiumAdsEnabled(
  premiumVersionManager: PremiumVersionManager,
  scope: ScopedCoroutineScope<AppScope>
) = premiumVersionManager.isPremiumVersion
  .map { AdsEnabled(!it) }
  .stateIn(scope, SharingStarted.Eagerly, AdsEnabled(false))

@Serializable data class PremiumVersionPrefs(val wasPremiumVersion: Boolean = false) {
  @Provide companion object {
    @Provide val dataStoreModule = DataStoreModule("premium_version_prefs") {
      PremiumVersionPrefs()
    }
  }
}

fun interface PremiumDowngradeHandler {
  suspend operator fun invoke()

  @Provide companion object {
    @Provide val defaultHandlers get() = emptyList<PremiumDowngradeHandler>()
  }
}

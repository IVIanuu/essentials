/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Eager
import com.ivianuu.essentials.ads.AdsEnabled
import com.ivianuu.essentials.billing.BillingService
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.data.DataStoreModule
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.UiScopeOwner
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface PremiumVersionManager {
  val premiumSkuDetails: Flow<SkuDetails>

  val isPremiumVersion: Flow<Boolean>

  suspend fun purchasePremiumVersion(): Boolean

  suspend fun consumePremiumVersion(): Boolean

  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R?
}

@Provide @Eager<AppScope> class PremiumVersionManagerImpl(
  private val appUiStarter: AppUiStarter,
  private val billingService: BillingService,
  private val downgradeHandlers: () -> List<PremiumDowngradeHandler>,
  private val logger: Logger,
  private val pref: DataStore<PremiumVersionPrefs>,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val screenUnlocker: ScreenUnlocker,
  private val toaster: Toaster
) : PremiumVersionManager {
  override val premiumSkuDetails: Flow<SkuDetails> =
    flow { emit(billingService.getSkuDetails(premiumVersionSku)!!) }

  override val isPremiumVersion: Flow<Boolean> = combine(
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
          downgradeHandlers().parForEach { it() }
        }
        pref.updateData {
          copy(wasPremiumVersion = isPremiumVersion)
        }
      }
    }
    .shareIn(scope, SharingStarted.Eagerly, 1)

  override suspend fun purchasePremiumVersion() =
    billingService.purchase(premiumVersionSku, true, true)

  override suspend fun consumePremiumVersion() = billingService.consumePurchase(premiumVersionSku)

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    scope.launch {
      toaster(com.ivianuu.essentials.premium.R.string.es_premium_version_hint)
      if (!screenUnlocker()) return@launch
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

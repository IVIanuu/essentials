/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.unlock.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Tag annotation class PremiumVersionSkuTag
typealias PremiumVersionSku = @PremiumVersionSkuTag Sku

@Tag annotation class OldPremiumVersionSkuTag {
  companion object {
    @Provide val defaultOldPermissionVersionSkus: List<OldPremiumVersionSku>
      get() = emptyList()
  }
}
typealias OldPremiumVersionSku = @OldPremiumVersionSkuTag Sku

interface PremiumVersionManager {
  val premiumSkuDetails: Flow<SkuDetails>

  val isPremiumVersion: Flow<Boolean>

  suspend fun purchasePremiumVersion(): Boolean

  suspend fun consumePremiumVersion(): Boolean

  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R?
}

@Provide @Scoped<AppScope> class PremiumVersionManagerImpl(
  private val appUiStarter: AppUiStarter,
  private val consumePurchase: ConsumePurchaseUseCase,
  private val getSkuDetails: GetSkuDetailsUseCase,
  private val navigator: Navigator,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>,
  isPurchased: (Sku) -> Flow<IsPurchased>,
  private val screenUnlocker: ScreenUnlocker,
  private val purchase: PurchaseUseCase,
  private val scope: NamedCoroutineScope<AppScope>
) : PremiumVersionManager {
  override val premiumSkuDetails: Flow<SkuDetails>
    get() = flow { emit(getSkuDetails(premiumVersionSku)!!) }

  override val isPremiumVersion: Flow<Boolean> = combine(
    isPurchased(premiumVersionSku),
    if (oldPremiumVersionSkus.isNotEmpty())
      combine(oldPremiumVersionSkus.map { isPurchased(it) }) {
        it.any { it.value }
      }
    else flowOf(false)
  ) { a, b -> a.value || b }
    .shareIn(scope, SharingStarted.Eagerly, 1)

  override suspend fun purchasePremiumVersion() = purchase(premiumVersionSku, true, true)

  override suspend fun consumePremiumVersion() = consumePurchase(premiumVersionSku)

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    scope.launch {
      if (!screenUnlocker()) return@launch
      appUiStarter()
      navigator.push(GoPremiumKey(showTryBasicOption = false))
    }

    return null
  }
}

@Provide fun showAds(premiumVersionManager: PremiumVersionManager): Flow<ShowAds> =
  premiumVersionManager.isPremiumVersion.map { ShowAds(!it) }

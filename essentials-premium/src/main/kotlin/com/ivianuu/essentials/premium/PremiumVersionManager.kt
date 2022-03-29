/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.unlock.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

interface PremiumVersionManager {
  val premiumSkuDetails: Flow<SkuDetails>

  val isPremiumVersion: Flow<Boolean>

  suspend fun purchasePremiumVersion(): Boolean

  suspend fun consumePremiumVersion(): Boolean

  suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R?
}

@Provide @Eager<AppScope> class PremiumVersionManagerImpl(
  private val appUiStarter: AppUiStarter,
  private val consumePurchase: ConsumePurchaseUseCase,
  private val downgradeHandlers: () -> List<PremiumDowngradeHandler>,
  private val getSkuDetails: GetSkuDetailsUseCase,
  private val navigator: Navigator,
  private val pref: DataStore<PremiumPrefs>,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>,
  isPurchased: (Sku) -> Flow<IsPurchased>,
  private val screenUnlocker: ScreenUnlocker,
  private val purchase: PurchaseUseCase,
  private val RP: ResourceProvider,
  private val scope: NamedCoroutineScope<AppScope>,
  private val toaster: Toaster
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
    .onEach { isPremiumVersion ->
      scope.launch {
        if (!isPremiumVersion && pref.data.first().wasPremiumVersion) {
          downgradeHandlers().parForEach { it() }
        }
        pref.updateData {
          copy(wasPremiumVersion = isPremiumVersion)
        }
      }
    }
    .shareIn(scope, SharingStarted.Eagerly, 1)

  override suspend fun purchasePremiumVersion() = purchase(premiumVersionSku, true, true)

  override suspend fun consumePremiumVersion() = consumePurchase(premiumVersionSku)

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    scope.launch {
      showToast(com.ivianuu.essentials.premium.R.string.es_premium_version_hint)
      if (!screenUnlocker()) return@launch
      appUiStarter()
      navigator.push(GoPremiumKey(showTryBasicOption = false))
    }

    return null
  }
}

@Tag annotation class PremiumVersionSkuTag
typealias PremiumVersionSku = @PremiumVersionSkuTag Sku

@Tag annotation class OldPremiumVersionSkuTag {
  companion object {
    @Provide val defaultOldPermissionVersionSkus: List<OldPremiumVersionSku>
      get() = emptyList()
  }
}
typealias OldPremiumVersionSku = @OldPremiumVersionSkuTag Sku

@Provide fun showAds(
  premiumVersionManager: PremiumVersionManager,
  scope: NamedCoroutineScope<AppScope>
): StateFlow<ShowAds> = premiumVersionManager.isPremiumVersion
  .map { ShowAds(!it) }
  .stateIn(scope, SharingStarted.Eagerly, ShowAds(false))

@Serializable data class PremiumPrefs(val wasPremiumVersion: Boolean = false) {
  companion object {
    @Provide val prefModule = PrefModule { PremiumPrefs() }
  }
}

fun interface PremiumDowngradeHandler : suspend () -> Unit {
  companion object {
    @Provide val defaultHandlers: List<PremiumDowngradeHandler>
      get() = emptyList()
  }
}

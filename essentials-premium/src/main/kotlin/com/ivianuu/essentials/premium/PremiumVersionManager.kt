/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.ads.ShowAds
import com.ivianuu.essentials.android.prefs.DataStoreModule
import com.ivianuu.essentials.billing.ConsumePurchaseUseCase
import com.ivianuu.essentials.billing.GetSkuDetailsUseCase
import com.ivianuu.essentials.billing.IsPurchased
import com.ivianuu.essentials.billing.PurchaseUseCase
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Eager
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
  private val L: Logger,
  private val RP: ResourceProvider,
  private val scope: NamedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : PremiumVersionManager {
  override val premiumSkuDetails: Flow<SkuDetails>
    get() = flow { emit(getSkuDetails(premiumVersionSku)!!) }

  override val isPremiumVersion: Flow<Boolean> = combine(
    isPurchased(premiumVersionSku),
    if (oldPremiumVersionSkus.isNotEmpty())
      combine(oldPremiumVersionSkus.map { isPurchased(it) }).map {
        it.any { it.value }
      }
    else flowOf(false)
  ) { a, b -> a.value || b }
    .onEach { isPremiumVersion ->
      scope.launch {
        if (!isPremiumVersion && pref.data.first().wasPremiumVersion) {
          log { "handle premium version downgrade" }
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

@Serializable data class PremiumPrefs(
  val wasPremiumVersion: Boolean = false,
  val firstStart: Boolean = true
) {
  companion object {
    @Provide val prefModule = DataStoreModule("premium_prefs") { PremiumPrefs() }
  }
}

fun interface PremiumDowngradeHandler : suspend () -> Unit {
  companion object {
    @Provide val defaultHandlers: List<PremiumDowngradeHandler>
      get() = emptyList()
  }
}

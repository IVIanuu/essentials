/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ads.ShowAds
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.billing.BillingService
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
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.Eager
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.inject
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

context(
AppUiStarter,
BillingService,
Logger,
NamedCoroutineScope<AppScope>,
ScreenUnlocker,
ToastContext)
@Provide @Eager<AppScope> class PremiumVersionManagerImpl(
  private val downgradeHandlers: () -> List<PremiumDowngradeHandler>,
  private val navigator: Navigator,
  private val pref: DataStore<PremiumPrefs>,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>
) : PremiumVersionManager {
  override val premiumSkuDetails: Flow<SkuDetails>
    get() = flow { emit(getSkuDetails(premiumVersionSku)!!) }

  override val isPremiumVersion: Flow<Boolean> = combine(
    isPurchased(premiumVersionSku),
    if (oldPremiumVersionSkus.isNotEmpty())
      combine(oldPremiumVersionSkus.map { isPurchased(it) }).map {
        it.any { it }
      }
    else flowOf(false)
  ) { a, b -> a || b }
    .onEach { isPremiumVersion ->
      launch {
        if (!isPremiumVersion && pref.data.first().wasPremiumVersion) {
          log { "handle premium version downgrade" }
          downgradeHandlers().parForEach { it() }
        }
        pref.updateData {
          copy(wasPremiumVersion = isPremiumVersion)
        }
      }
    }
    .shareIn(inject(), SharingStarted.Eagerly, 1)

  override suspend fun purchasePremiumVersion() = purchase(premiumVersionSku, true, true)

  override suspend fun consumePremiumVersion() = consumePurchase(premiumVersionSku)

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    launch {
      showToast(com.ivianuu.essentials.premium.R.string.es_premium_version_hint)
      if (!unlockScreen()) return@launch
      startAppUi()
      navigator.push(GoPremiumKey(showTryBasicOption = false))
    }

    return null
  }
}

@Tag annotation class PremiumVersionSkuTag {
  companion object {
    @Provide val default get() = Sku("premium_version", Sku.Type.SUBS)
  }
}
typealias PremiumVersionSku = @PremiumVersionSkuTag Sku

@Tag annotation class OldPremiumVersionSkuTag {
  companion object {
    @Provide val defaultOldPermissionVersionSkus get() = emptyList<OldPremiumVersionSku>()
  }
}
typealias OldPremiumVersionSku = @OldPremiumVersionSkuTag Sku

context(NamedCoroutineScope<AppScope>, PremiumVersionManager)
    @Provide fun showAds(): StateFlow<ShowAds> = isPremiumVersion
  .map { ShowAds(!it) }
  .stateIn(this@NamedCoroutineScope, SharingStarted.Eagerly, ShowAds(false))

@Serializable data class PremiumPrefs(val wasPremiumVersion: Boolean = false) {
  companion object {
    @Provide val prefModule = PrefModule { PremiumPrefs() }
  }
}

fun interface PremiumDowngradeHandler {
  suspend operator fun invoke()

  companion object {
    @Provide val defaultHandlers get() = emptyList<PremiumDowngradeHandler>()
  }
}

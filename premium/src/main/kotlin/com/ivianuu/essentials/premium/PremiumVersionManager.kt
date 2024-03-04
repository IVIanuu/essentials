/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.premium

import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import co.touchlab.kermit.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ads.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

interface PremiumVersionManager {
  val premiumSkuDetails: Flow<SkuDetails>
  val isPremiumVersion: StateFlow<Boolean>

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
  private val pref: DataStore<PremiumVersionPrefs>,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku>,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val toaster: Toaster
) : PremiumVersionManager {
  override val premiumSkuDetails: Flow<SkuDetails> =
    flow { emit(billingManager.getSkuDetails(premiumVersionSku)!!) }

  override val isPremiumVersion = scope.moleculeStateFlow {
    val isPremiumVersion = (oldPremiumVersionSkus + premiumVersionSku)
      .map { billingManager.isPurchased(it).state(null) == true }
      .any()

    LaunchedEffect(isPremiumVersion) {
      if (!isPremiumVersion && pref.data.first().wasPremiumVersion) {
        logger.d { "handle premium version downgrade" }
        downgradeHandlers().parMap { it() }
      }
      pref.updateData { copy(wasPremiumVersion = isPremiumVersion) }
    }

    isPremiumVersion
  }

  override suspend fun purchasePremiumVersion() =
    billingManager.purchase(premiumVersionSku, true, true)

  override suspend fun consumePremiumVersion() = billingManager.consumePurchase(premiumVersionSku)

  override suspend fun <R> runOnPremiumOrShowHint(block: suspend () -> R): R? {
    if (isPremiumVersion.first()) return block()

    scope.launch {
      toaster("This functionality is only available in the premium version!")
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

@Provide fun premiumAdsEnabled(
  premiumVersionManager: PremiumVersionManager,
  scope: ScopedCoroutineScope<AppScope>
): @Scoped<AppScope> State<AdsEnabled> {
  val state = mutableStateOf(AdsEnabled(false))
  scope.launch {
    premiumVersionManager.isPremiumVersion
      .map { AdsEnabled(!it) }
      .collect { state.value = it }
  }
  return state
}

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

package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*

typealias GetSkuDetailsUseCase = suspend (Sku) -> SkuDetails?

@Provide fun getSkuDetailsUseCase(context: BillingContext): GetSkuDetailsUseCase = { sku ->
  context.withConnection {
    billingClient.querySkuDetails(sku.toSkuDetailsParams())
      .skuDetailsList
      ?.firstOrNull { it.sku == sku.skuString }
      .also { d(logger = logger) { "got sku details $it for $sku" } }
  }
}

typealias PurchaseUseCase = suspend (Sku, Boolean, Boolean) -> Boolean

@Provide fun purchaseUseCase(
  acknowledgePurchase: AcknowledgePurchaseUseCase,
  appUiStarter: AppUiStarter,
  context: BillingContext,
  consumePurchase: ConsumePurchaseUseCase,
  getSkuDetails: GetSkuDetailsUseCase
): PurchaseUseCase = { sku, acknowledge, consumeOldPurchaseIfUnspecified ->
  context.withConnection {
    d(logger = logger) {
      "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified"
    }
    if (consumeOldPurchaseIfUnspecified) {
      val oldPurchase = getPurchase(sku)
      if (oldPurchase != null) {
        if (oldPurchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
          consumePurchase(sku)
        }
      }
    }

    val activity = appUiStarter()

    val skuDetails = getSkuDetails(sku)
      ?: return@withConnection false

    val billingFlowParams = BillingFlowParams.newBuilder()
      .setSkuDetails(skuDetails)
      .build()

    val result = billingClient.launchBillingFlow(activity, billingFlowParams)
    if (result.responseCode != BillingClient.BillingResponseCode.OK)
      return@withConnection false

    refreshes.first()

    val success = getIsPurchased(sku)

    return@withConnection if (success && acknowledge) acknowledgePurchase(sku) else success
  }
}

typealias ConsumePurchaseUseCase = suspend (Sku) -> Boolean

@Provide fun consumePurchaseUseCase(context: BillingContext): ConsumePurchaseUseCase = { sku ->
  context.withConnection {
    val purchase = getPurchase(sku) ?: return@withConnection false

    val consumeParams = ConsumeParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.consumePurchase(consumeParams)

    d(logger = logger) {
      "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
    }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(Unit)
    return@withConnection success
  }
}

typealias AcknowledgePurchaseUseCase = suspend (Sku) -> Boolean

@Provide fun acknowledgePurchaseUseCase(context: BillingContext): AcknowledgePurchaseUseCase =
  { sku ->
    context.withConnection {
      val purchase = getPurchase(sku)
        ?: return@withConnection false

      if (purchase.isAcknowledged) return@withConnection true

      val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
        .setPurchaseToken(purchase.purchaseToken)
        .build()

      val result = billingClient.acknowledgePurchase(acknowledgeParams)

      d(logger = logger) {
        "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
      }

      val success = result.responseCode == BillingClient.BillingResponseCode.OK
      if (success) refreshes.emit(Unit)
      return@withConnection success
    }
  }

typealias IsPurchased = Boolean

@Provide fun isPurchased(
  appForegroundState: Flow<AppForegroundState>,
  context: BillingContext,
  sku: Sku
): Flow<IsPurchased> = merge(
  appForegroundState
    .filter { it == AppForegroundState.FOREGROUND },
  context.refreshes
)
  .onStart { emit(Unit) }
  .map {
    context.withConnection {
      getIsPurchased(sku)
    }
  }
  .distinctUntilChanged()
  .onEach { d(logger = context.logger) { "is purchased flow for $sku -> $it" } }

private fun BillingContext.getIsPurchased(sku: Sku): Boolean {
  val purchase = getPurchase(sku) ?: return false
  val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
  d(logger = logger) { "get is purchased for $sku result is $isPurchased for $purchase" }
  return isPurchased
}

private fun BillingContext.getPurchase(sku: Sku): Purchase? =
  billingClient.queryPurchases(sku.type.value)
    .purchasesList
    ?.firstOrNull { it.sku == sku.skuString }
    .also { d(logger = logger) { "got purchase $it for $sku" } }

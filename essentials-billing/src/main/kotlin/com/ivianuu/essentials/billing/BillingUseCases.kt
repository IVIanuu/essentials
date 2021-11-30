/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import androidx.compose.runtime.Composable
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.querySkuDetails
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.state.asComposedFlow
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

fun interface GetSkuDetailsUseCase : suspend (Sku) -> SkuDetails?

@Provide fun getSkuDetailsUseCase(context: BillingContext) = GetSkuDetailsUseCase { sku ->
  context.withConnection {
    billingClient.querySkuDetails(sku.toSkuDetailsParams())
      .skuDetailsList
      ?.firstOrNull { it.sku == sku.skuString }
      .also { log { "got sku details $it for $sku" } }
  }
}

fun interface PurchaseUseCase : suspend (Sku, Boolean, Boolean) -> Boolean

@Provide fun purchaseUseCase(
  acknowledgePurchase: AcknowledgePurchaseUseCase,
  appUiStarter: AppUiStarter,
  context: BillingContext,
  consumePurchase: ConsumePurchaseUseCase,
  getSkuDetails: GetSkuDetailsUseCase
) = PurchaseUseCase { sku, acknowledge, consumeOldPurchaseIfUnspecified ->
  context.withConnection {
    log {
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
  } ?: false
}

fun interface ConsumePurchaseUseCase : suspend (Sku) -> Boolean

@Provide fun consumePurchaseUseCase(context: BillingContext) = ConsumePurchaseUseCase { sku ->
  context.withConnection {
    val purchase = getPurchase(sku) ?: return@withConnection false

    val consumeParams = ConsumeParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.consumePurchase(consumeParams)

    log {
      "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
    }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@withConnection success
  } ?: false
}

fun interface AcknowledgePurchaseUseCase : suspend (Sku) -> Boolean

@Provide fun acknowledgePurchaseUseCase(context: BillingContext) = AcknowledgePurchaseUseCase { sku ->
  context.withConnection {
    val purchase = getPurchase(sku)
      ?: return@withConnection false

    if (purchase.isAcknowledged) return@withConnection true

    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.acknowledgePurchase(acknowledgeParams)

    log {
      "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
    }

    val success = result.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@withConnection success
  } ?: false
}

@JvmInline value class IsPurchased(val value: Boolean)

@Provide fun isPurchased(
  appForegroundState: @Composable () -> AppForegroundState,
  context: BillingContext,
  sku: Sku
): Flow<IsPurchased> = merge(
  appForegroundState.asComposedFlow()
    .filter { it == AppForegroundState.FOREGROUND },
  context.refreshes
)
  .onStart { emit(Unit) }
  .map {
    context.withConnection {
      IsPurchased(getIsPurchased(sku))
    } ?: IsPurchased(false)
  }
  .distinctUntilChanged()
  .onEach { log { "is purchased flow for $sku -> $it" } }

private fun BillingContext.getIsPurchased(sku: Sku): Boolean {
  val purchase = getPurchase(sku) ?: return false
  val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
  log { "get is purchased for $sku result is $isPurchased for $purchase" }
  return isPurchased
}

private fun BillingContext.getPurchase(sku: Sku): Purchase? =
  billingClient.queryPurchases(sku.type.value)
    .purchasesList
    ?.firstOrNull { it.sku == sku.skuString }
    .also { log { "got purchase $it for $sku" } }

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

typealias PurchaseUseCase = suspend (Sku, Boolean, Boolean) -> Boolean

@Given
fun purchaseUseCase(
    @Given acknowledgePurchase: AcknowledgePurchaseUseCase,
    @Given appUiStarter: AppUiStarter,
    @Given context: BillingContext,
    @Given consumePurchase: ConsumePurchaseUseCase
): PurchaseUseCase = { sku, acknowledge, consumeOldPurchaseIfUnspecified ->
    context.withConnection {
        context.logger.d {
            "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified"
        }
        if (consumeOldPurchaseIfUnspecified) {
            val oldPurchase = context.getPurchase(sku)
            if (oldPurchase != null) {
                if (oldPurchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    consumePurchase(sku)
                }
            }
        }

        val activity = appUiStarter()

        val skuDetails = context.getSkuDetails(sku)
            ?: return@withConnection false

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        val result = context.billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK)
            return@withConnection false

        context.refreshes.first()

        val success = context.getIsPurchased(sku)

        return@withConnection if (success && acknowledge) acknowledgePurchase(sku) else success
    }
}

typealias ConsumePurchaseUseCase = suspend (Sku) -> Boolean

@Given
fun consumePurchaseUseCase(@Given context: BillingContext): ConsumePurchaseUseCase = { sku ->
    context.withConnection {
        val purchase = context.getPurchase(sku) ?: return@withConnection false

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = context.billingClient.consumePurchase(consumeParams)

        context.logger.d {
            "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
        }

        val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
        if (success) context.refreshes.emit(Unit)
        return@withConnection success
    }
}

typealias AcknowledgePurchaseUseCase = suspend (Sku) -> Boolean

@Given
fun acknowledgePurchaseUseCase(@Given context: BillingContext): AcknowledgePurchaseUseCase = { sku ->
    context.withConnection {
        val purchase = context.getPurchase(sku)
            ?: return@withConnection false

        if (purchase.isAcknowledged) return@withConnection true

        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = context.billingClient.acknowledgePurchase(acknowledgeParams)

        context.logger.d {
            "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
        }

        val success = result.responseCode == BillingClient.BillingResponseCode.OK
        if (success) context.refreshes.emit(Unit)
        return@withConnection success
    }
}

typealias IsPurchased = Boolean

@Given
fun isPurchased(
    @Given appForegroundState: Flow<AppForegroundState>,
    @Given context: BillingContext,
    @Given sku: Sku
): Flow<IsPurchased> = merge(
    appForegroundState
        .filter { it == AppForegroundState.FOREGROUND },
    context.refreshes
)
    .onStart { emit(Unit) }
    .map { context.getIsPurchased(sku) }
    .distinctUntilChanged()
    .onEach { context.logger.d { "is purchased flow for $sku -> $it" } }

private fun BillingContext.getIsPurchased(sku: Sku): Boolean {
    val purchase = getPurchase(sku) ?: return false
    val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
    logger.d { "get is purchased for $sku result is $isPurchased for $purchase" }
    return isPurchased
}

private fun BillingContext.getPurchase(sku: Sku): Purchase? =
    billingClient.queryPurchases(sku.type.value)
        .purchasesList
        ?.firstOrNull { it.sku == sku.skuString }
        .also { logger.d { "got purchase $it for $sku" } }

private suspend fun BillingContext.getSkuDetails(sku: Sku): SkuDetails? =
    billingClient.querySkuDetails(sku.toSkuDetailsParams())
        .skuDetailsList
        ?.firstOrNull { it.sku == sku.skuString }
        .also { logger.d { "got sku details $it for $sku" } }

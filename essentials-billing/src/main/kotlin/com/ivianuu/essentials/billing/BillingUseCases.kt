package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
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
        logger.d {
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

        val skuDetails = getSkuDetails(sku) ?: return@withConnection false

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) return@withConnection false

        refreshes.first()

        val success = getIsPurchased(sku)

        if (!acknowledge) return@withConnection success

        if (success) acknowledgePurchase(sku) else return@withConnection false
    }
}

typealias ConsumePurchaseUseCase = suspend (Sku) -> Boolean

@Given
fun consumePurchaseUseCase(@Given context: BillingContext): ConsumePurchaseUseCase = { sku ->
    context.withConnection {
        val purchase = getPurchase(sku) ?: return@withConnection false

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.consumePurchase(consumeParams)

        logger.d {
            "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
        }

        val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
        if (success) refreshes.emit(Unit)
        return@withConnection success
    }
}

typealias AcknowledgePurchaseUseCase = suspend (Sku) -> Boolean

@Given
fun acknowledgePurchaseUseCase(@Given context: BillingContext): AcknowledgePurchaseUseCase = { sku ->
    context.withConnection {
        val purchase = getPurchase(sku) ?: return@withConnection false

        if (purchase.isAcknowledged) return@withConnection true

        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.acknowledgePurchase(acknowledgeParams)

        logger.d {
            "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
        }

        val success = result.responseCode == BillingClient.BillingResponseCode.OK
        if (success) refreshes.emit(Unit)
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
    .map {
        context.withConnection {
            getIsPurchased(sku)
        }
    }
    .distinctUntilChanged()
    .onEach { context.logger.d { "is purchased flow for $sku -> $it" } }

@Given
@Scoped<AppGivenScope>
class BillingContext(
    @Given billingClientFactory: (@Given PurchasesUpdatedListener) -> BillingClient,
    @Given private val dispatcher: IODispatcher,
    @Given val logger: Logger,
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>
) {
    val billingClient = billingClientFactory { _, _ -> refreshes.tryEmit(Unit) }

    private var isConnected = false
    private val connectionMutex = Mutex()

    val refreshes = EventFlow<Unit>()

    suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R =
        withContext(scope.coroutineContext + dispatcher) {
            ensureConnected()
            block()
        }

    private suspend fun ensureConnected() = connectionMutex.withLock {
        if (isConnected) return@withLock
        if (billingClient.isReady) {
            isConnected = true
            return@withLock
        }
        suspendCoroutine<Unit> { continuation ->
            logger.d { "start connection" }
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(result: BillingResult) {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            logger.d { "connected" }
                            isConnected = true
                            continuation.resume(Unit)
                        } else {
                            logger.d { "connecting failed ${result.responseCode} ${result.debugMessage}" }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        logger.d { "on billing service disconnected" }
                    }
                }
            )
        }
    }
}

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

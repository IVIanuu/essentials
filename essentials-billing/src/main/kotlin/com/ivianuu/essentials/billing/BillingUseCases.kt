package com.ivianuu.essentials.billing

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.querySkuDetails
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias PurchaseUseCase = suspend (Sku, Boolean, Boolean) -> Boolean

@Given
fun BillingContext.purchaseUseCase(
    @Given acknowledgePurchase: AcknowledgePurchaseUseCase,
    @Given appUiStarter: AppUiStarter,
    @Given consumePurchase: ConsumePurchaseUseCase
): PurchaseUseCase = { sku, acknowledge, consumeOldPurchaseIfUnspecified ->
    withConnection {
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
val BillingContext.consumePurchaseUseCase: ConsumePurchaseUseCase
    get() = { sku ->
        withConnection {
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
val BillingContext.acknowledgePurchaseUseCase: AcknowledgePurchaseUseCase
    get() = { sku ->
        withConnection {
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
fun BillingContext.isPurchased(
    @Given appForegroundState: Flow<AppForegroundState>,
    @Given sku: Sku
): Flow<IsPurchased> = merge(
    appForegroundState
        .filter { it == AppForegroundState.FOREGROUND },
    refreshes
)
    .onStart { emit(Unit) }
    .map {
        withConnection {
            getIsPurchased(sku)
        }
    }
    .distinctUntilChanged()
    .onEach { logger.d { "is purchased flow for $sku -> $it" } }

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

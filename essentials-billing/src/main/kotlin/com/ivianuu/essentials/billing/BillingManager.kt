package com.ivianuu.essentials.billing

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
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
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface BillingManager {

    suspend fun purchase(
        sku: Sku,
        acknowledge: Boolean = true,
        consumeOldPurchaseIfUnspecified: Boolean = true,
    ): Boolean

    suspend fun consumePurchase(sku: Sku): Boolean

    suspend fun acknowledgePurchase(sku: Sku): Boolean

    fun isPurchased(sku: Sku): Flow<Boolean>

    suspend fun isBillingFeatureSupported(feature: BillingFeature): Boolean

}

@ImplBinding(ApplicationComponent::class)
class RealBillingManager(
    billingClientFactory: (PurchasesUpdatedListener) -> BillingClient,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val startUi: startUi,
) : BillingManager {

    private val billingClient = billingClientFactory { _, _ ->
        refreshes.offer(Unit)
    }

    private val refreshes = EventFlow<Unit>()
    private val connecting = AtomicBoolean(false)

    override suspend fun purchase(
        sku: Sku,
        acknowledge: Boolean,
        consumeOldPurchaseIfUnspecified: Boolean,
    ): Boolean = withContext(dispatchers.default) {
        logger.d("purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified")

        if (consumeOldPurchaseIfUnspecified) {
            val oldPurchase = getPurchase(sku)
            if (oldPurchase != null) {
                if (oldPurchase.realPurchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    consumePurchase(sku)
                }
            }
        }

        val activity = startUi()

        ensureConnected()

        val skuDetails = getSkuDetails(sku) ?: return@withContext false

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) return@withContext false

        refreshes.take(1).collect()

        val success = getIsPurchased(sku)

        if (!acknowledge) return@withContext success

        return@withContext if (success) acknowledgePurchase(sku) else return@withContext false
    }

    override suspend fun consumePurchase(sku: Sku): Boolean = withContext(dispatchers.default) {
        ensureConnected()

        val purchase = getPurchase(sku) ?: return@withContext false

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.consumePurchase(consumeParams)

        logger.d("consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}")

        val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
        if (success) refreshes.offer(Unit)
        return@withContext success
    }

    override suspend fun acknowledgePurchase(sku: Sku): Boolean = withContext(dispatchers.default) {
        ensureConnected()
        val purchase = getPurchase(sku) ?: return@withContext false

        if (purchase.isAcknowledged) return@withContext true

        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.acknowledgePurchase(acknowledgeParams)

        logger.d("acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}")

        val success = result.responseCode == BillingClient.BillingResponseCode.OK
        if (success) refreshes.offer(Unit)
        return@withContext success
    }

    override fun isPurchased(sku: Sku): Flow<Boolean> {
        val appMovedToForegroundFlow = callbackFlow<Unit> {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) offerSafe(Unit)
            }
            withContext(dispatchers.main) {
                ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
            }
            awaitClose()
            withContext(dispatchers.main + NonCancellable) {
                ProcessLifecycleOwner.get().lifecycle.removeObserver(observer)
            }
        }

        return merge(
            appMovedToForegroundFlow,
            refreshes
        )
            .onStart { emit(Unit) }
            .map { getIsPurchased(sku) }
            .distinctUntilChanged()
            .onEach { logger.d("is purchased flow for $sku -> $it") }
    }

    override suspend fun isBillingFeatureSupported(feature: BillingFeature): Boolean =
        withContext(dispatchers.default) {
            ensureConnected()
            val result = billingClient.isFeatureSupported(feature.value)
            logger.d("is feature supported $feature ? ${result.responseCode} ${result.debugMessage}")
            return@withContext result.responseCode == BillingClient.BillingResponseCode.OK
        }

    private suspend fun getIsPurchased(sku: Sku): Boolean = withContext(dispatchers.default) {
        ensureConnected()
        val purchase = getPurchase(sku) ?: return@withContext false
        val isPurchased = purchase.realPurchaseState == Purchase.PurchaseState.PURCHASED
        logger.d("get is purchased for $sku result is $isPurchased for $purchase")
        return@withContext isPurchased
    }

    private suspend fun getPurchase(sku: Sku): Purchase? = withContext(dispatchers.io) {
        ensureConnected()
        billingClient.queryPurchases(sku.type.value)
            .purchasesList
            ?.firstOrNull { it.sku == sku.skuString }
            .also { logger.d("got purchase $it for $sku") }
    }

    private suspend fun getSkuDetails(sku: Sku): SkuDetails? = withContext(dispatchers.io) {
        ensureConnected()
        billingClient.querySkuDetails(sku.toSkuDetailsParams())
            .skuDetailsList
            ?.firstOrNull { it.sku == sku.skuString }
            .also { logger.d("got sku details $it for $sku") }
    }

    private suspend fun ensureConnected() = withContext(dispatchers.io) {
        if (billingClient.isReady) return@withContext
        if (connecting.getAndSet(true)) return@withContext
        suspendCoroutine<Unit> { continuation ->
            logger.d("start connection")
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(result: BillingResult) {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            logger.d("connected")
                            continuation.resume(Unit)
                        } else {
                            logger.d("connecting failed ${result.responseCode} ${result.debugMessage}")
                        }

                        connecting.set(false)
                    }

                    override fun onBillingServiceDisconnected() {
                        logger.d("on billing service disconnected")
                    }
                }
            )
        }
    }

    private val Purchase.realPurchaseState: Int
        get() = JSONObject(originalJson).optInt("purchaseState", 0)

}

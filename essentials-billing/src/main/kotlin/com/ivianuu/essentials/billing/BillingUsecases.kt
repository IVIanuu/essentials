package com.ivianuu.essentials.billing

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.ui.core.ContextAmbient
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.querySkuDetails
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Reader
suspend fun purchase(
    sku: Sku,
    acknowledge: Boolean = true,
    consumeOldPurchaseIfUnspecified: Boolean = true
): Boolean = withContext(dispatchers.default) {
    d { "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified" }

    if (consumeOldPurchaseIfUnspecified) {
        val oldPurchase = getPurchase(sku)
        if (oldPurchase != null) {
            if (oldPurchase.realPurchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                consumePurchase(sku)
            }
        }
    }

    startUi()

    val activity = suspendCancellableCoroutine<Activity> { cont ->
        navigator.push(
            Route(opaque = true) {
                cont.resume(ContextAmbient.current as Activity)
            }
        )
    }

    ensureConnected()

    val skuDetails = getSkuDetails(sku) ?: return@withContext false

    val billingFlowParams = BillingFlowParams.newBuilder()
        .setSkuDetails(skuDetails)
        .build()

    val result = given<BillingClient>().launchBillingFlow(activity, billingFlowParams)
    d { "launch billing flow result ${result.responseCode} ${result.debugMessage}" }
    if (result.responseCode != BillingClient.BillingResponseCode.OK) return@withContext false

    val success = refreshTrigger
        .take(1)
        .map { getIsPurchased(sku) }
        .first()

    d { "purchase finished $sku -> success ? $success" }

    if (!acknowledge) return@withContext success

    return@withContext if (success) acknowledgePurchase(sku) else return@withContext false
}

@Reader
suspend fun consumePurchase(sku: Sku): Boolean = withContext(dispatchers.default) {
    ensureConnected()

    val purchase = getPurchase(sku) ?: return@withContext false

    val consumeParams = ConsumeParams.newBuilder()
        .setPurchaseToken(purchase.purchaseToken)
        .build()

    val result = given<BillingClient>().consumePurchase(consumeParams)

    d { "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}" }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshTrigger.offer(Unit)
    return@withContext success
}

@Reader
suspend fun acknowledgePurchase(sku: Sku): Boolean = withContext(dispatchers.default) {
    ensureConnected()
    val purchase = getPurchase(sku) ?: return@withContext false

    if (purchase.isAcknowledged) return@withContext true

    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
        .setPurchaseToken(purchase.purchaseToken)
        .build()

    val result = given<BillingClient>().acknowledgePurchase(acknowledgeParams)

    d { "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}" }

    val success = result.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshTrigger.offer(Unit)
    return@withContext success
}

@Reader
fun isPurchased(sku: Sku): Flow<Boolean> {
    val appMovedToForegroundFlow = callbackFlow<Unit> {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) offer(Unit)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        awaitClose { ProcessLifecycleOwner.get().lifecycle.removeObserver(observer) }
    }

    return merge(
        appMovedToForegroundFlow,
        refreshTrigger
    )
        .onEach { d { "is purchased flow for $sku -> refresh triggered" } }
        .onStart { emit(Unit) }
        .map { getIsPurchased(sku) }
        .onEach { d { "is purchased flow for $sku -> fetched value $it" } }
        .distinctUntilChanged()
        .onEach { d { "is purchased flow for $sku -> emit $it" } }
}

@Reader
suspend fun isBillingFeatureSupported(feature: BillingFeature): Boolean =
    withContext(dispatchers.default) {
        ensureConnected()
        val result = given<BillingClient>().isFeatureSupported(feature.value)
        d { "is feature supported $feature ? ${result.responseCode} ${result.debugMessage}" }
        return@withContext result.responseCode == BillingClient.BillingResponseCode.OK
    }

@Reader
private suspend fun getIsPurchased(sku: Sku): Boolean = withContext(dispatchers.default) {
    ensureConnected()
    val purchase = getPurchase(sku) ?: return@withContext false
    val isPurchased = purchase.realPurchaseState == Purchase.PurchaseState.PURCHASED
    d { "get is purchased for $sku result is $isPurchased for $purchase" }
    return@withContext isPurchased
}

@Reader
private suspend fun getPurchase(sku: Sku): Purchase? = withContext(dispatchers.io) {
    ensureConnected()
    given<BillingClient>().queryPurchases(sku.type.value)
        .purchasesList
        ?.firstOrNull { it.sku == sku.skuString }
        ?.also { d { "got purchase $it for $sku" } }
}

@Reader
private suspend fun getSkuDetails(sku: Sku): SkuDetails? = withContext(dispatchers.io) {
    ensureConnected()
    given<BillingClient>().querySkuDetails(sku.toSkuDetailsParams())
        .skuDetailsList
        ?.firstOrNull { it.sku == sku.skuString }
        .also { d { "got sku details $it for $sku" } }
}

private val connecting = AtomicBoolean(false)

@Reader
private suspend fun ensureConnected() = withContext(dispatchers.io) {
    val billingClient = given<BillingClient>()
    if (billingClient.isReady) return@withContext
    if (connecting.getAndSet(true)) return@withContext
    suspendCoroutine<Unit> { continuation ->
        d { "start connection" }
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        d { "connected" }
                        continuation.resume(Unit)
                    } else {
                        d { "connecting failed ${result.responseCode} ${result.debugMessage}" }
                    }

                    connecting.set(false)
                }

                override fun onBillingServiceDisconnected() {
                    d { "on billing service disconnected" }
                }
            }
        )
    }
}

private val Purchase.realPurchaseState: Int
    get() = JSONObject(originalJson).optInt("purchaseState", 0)

internal val refreshTrigger = EventFlow<Unit>()

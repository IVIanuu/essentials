/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.billing

import android.content.Context
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
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ApplicationScope
@Single
class PurchaseManager(
    billingClientProvider: Provider<BillingClient>,
    private val context: Context,
    private val dispatchers: AppDispatchers
) {

    private val updateListener = PurchasesUpdatedListener { result, purchases ->
        d { "on purchases update ${result.responseCode} ${result.debugMessage} $purchases" }
        updates.offer(Update(result.responseCode, purchases ?: emptyList()))
    }
    private val updates = BroadcastChannel<Update>(1)

    data class Update(
        val responseCode: Int,
        val purchases: List<Purchase>
    )

    private val billingClient = billingClientProvider { parametersOf(updateListener) }

    private val requests = ConcurrentHashMap<String, PurchaseRequest>()

    private data class PurchaseRequest(
        val sku: Sku,
        val result: CompletableDeferred<Boolean>
    )

    suspend fun purchase(
        sku: Sku,
        acknowledge: Boolean = true,
        consumeOldPurchaseIfUnspecified: Boolean = true
    ): Boolean = withContext(dispatchers.io) {
        val requestId = UUID.randomUUID().toString()
        val result = CompletableDeferred<Boolean>()
        requests[requestId] = PurchaseRequest(sku = sku, result = result)

        d { "purchase $sku -> acknowledge: $acknowledge, id: $requestId" }

        val oldPurchase = getPurchase(sku)
        if (oldPurchase != null) {
            if (oldPurchase.realPurchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                consume(sku)
            }
        }

        withContext(dispatchers.main) {
            PurchaseActivity.purchase(context, requestId)
        }

        val success = merge(
            updates.asFlow()
                .take(1)
                .map { update ->
                    update.purchases.any { purchase ->
                        purchase.sku == sku.skuString &&
                                purchase.realPurchaseState == Purchase.PurchaseState.PURCHASED
                    }
                },

            flow { emit(result.await()) }
        ).first()

        d { "purchase finished $sku -> success ? $success" }

        requests -= requestId

        if (!acknowledge) return@withContext success

        return@withContext if (success) acknowledge(sku) else return@withContext false
    }

    suspend fun consume(sku: Sku): Boolean = withContext(dispatchers.io) {
        ensureConnected()

        val purchase = getPurchase(sku) ?: return@withContext false

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.consumePurchase(consumeParams)

        d { "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}" }

        return@withContext result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    }

    suspend fun acknowledge(sku: Sku): Boolean = withContext(dispatchers.io) {
        ensureConnected()
        val purchase = getPurchase(sku) ?: return@withContext false

        if (purchase.isAcknowledged) return@withContext true

        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.acknowledgePurchase(acknowledgeParams)

        d { "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}" }

        return@withContext result.responseCode == BillingClient.BillingResponseCode.OK
    }

    internal suspend fun purchaseInternal(
        requestId: String,
        activity: PurchaseActivity
    ) = withContext(dispatchers.io) {
        d { "purchase internal $requests" }
        val request = requests[requestId] ?: return@withContext

        ensureConnected()

        val skuDetails = getSkuDetails(request.sku)
        if (skuDetails == null) {
            request.result.complete(false)
            return@withContext
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        d { "launch billing flow result $request ${result.responseCode} ${result.debugMessage}" }
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            request.result.complete(false)
        }
    }

    fun isPurchased(sku: Sku): Flow<Boolean> {
        val inForegroundFlow = callbackFlow {
            val observer = LifecycleEventObserver { source, _ ->
                offer(source.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            }
            ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
            awaitClose { ProcessLifecycleOwner.get().lifecycle.removeObserver(observer) }
        }.distinctUntilChanged()

        return inForegroundFlow
            .onEach { d { "is purchased flow for $sku -> in foreground changed $it" } }
            .map { Unit }
            .onStart { emit(Unit) }
            .flatMapLatest {
                updates.asFlow()
                    .onEach { d { "is purchased flow for $sku -> on update $it" } }
                    .map { update ->
                        update.purchases.any { purchase ->
                            purchase.sku == sku.skuString &&
                                    purchase.realPurchaseState == Purchase.PurchaseState.PURCHASED
                        }
                    }
                    .onStart { emit(getIsPurchased(sku)) }
                    .onEach { d { "is purchased flow for $sku -> emit $it" } }
            }
            .distinctUntilChanged()
    }

    suspend fun isFeatureSupported(feature: BillingFeature): Boolean = withContext(dispatchers.io) {
        ensureConnected()
        val result = billingClient.isFeatureSupported(feature.value)
        d { "is feature supported $feature ? ${result.responseCode} ${result.debugMessage}" }
        return@withContext result.responseCode == BillingClient.BillingResponseCode.OK
    }

    private suspend fun getIsPurchased(sku: Sku): Boolean = withContext(dispatchers.io) {
        ensureConnected()
        val purchase = getPurchase(sku) ?: return@withContext false
        val isPurchased = purchase.realPurchaseState == Purchase.PurchaseState.PURCHASED
        d { "get is purchased for $sku result is $isPurchased for $purchase" }
        return@withContext isPurchased
    }

    private suspend fun getPurchase(sku: Sku): Purchase? {
        ensureConnected()
        return billingClient.queryPurchases(sku.type.value)
            .purchasesList
            ?.firstOrNull { it.sku == sku.skuString }
            ?.also { d { "got purchase $it for $sku" } }
    }

    private suspend fun getSkuDetails(sku: Sku): SkuDetails? {
        ensureConnected()
        return billingClient.querySkuDetails(sku.toSkuDetailsParams())
            .skuDetailsList
            ?.firstOrNull { it.sku == sku.skuString }
            .also { d { "got sku details $it for $sku" } }
    }

    private val connecting = AtomicBoolean(false)

    private suspend fun ensureConnected() {
        if (billingClient.isReady) return
        if (connecting.getAndSet(true)) return
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
}

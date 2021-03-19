/*
 * Copyright 2020 Manuel Wrage
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

import com.android.billingclient.api.*
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.awaitAsync
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import com.ivianuu.injekt.scope.AppGivenScope
import kotlinx.coroutines.flow.*
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

@Scoped<AppGivenScope>
@Given
class BillingManagerImpl(
    @Given private val appForegroundState: Flow<AppForegroundState>,
    @Given private val appUiStarter: AppUiStarter,
    @Given billingClientFactory: (@Given PurchasesUpdatedListener) -> BillingClient,
    @Given private val defaultDispatcher: DefaultDispatcher,
    @Given private val ioDispatcher: IODispatcher,
    @Given private val logger: Logger,
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>
) : BillingManager {

    private val billingClient = billingClientFactory { _, _ ->
        refreshes.tryEmit(Unit)
    }

    private val refreshes = EventFlow<Unit>()
    private val connecting = AtomicBoolean(false)

    override suspend fun purchase(
        sku: Sku,
        acknowledge: Boolean,
        consumeOldPurchaseIfUnspecified: Boolean,
    ): Boolean = scope.awaitAsync(ioDispatcher) {
        logger.d {
            "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified"
        }

        if (consumeOldPurchaseIfUnspecified) {
            val oldPurchase = getPurchase(sku)
            if (oldPurchase != null) {
                if (oldPurchase.realPurchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    consumePurchase(sku)
                }
            }
        }

        val activity = appUiStarter()

        ensureConnected()

        val skuDetails = getSkuDetails(sku) ?: return@awaitAsync false

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) return@awaitAsync false

        refreshes.first()

        val success = getIsPurchased(sku)

        if (!acknowledge) return@awaitAsync success

        return@awaitAsync if (success) acknowledgePurchase(sku) else return@awaitAsync false
    }

    override suspend fun consumePurchase(sku: Sku): Boolean = scope.awaitAsync(defaultDispatcher) {
        ensureConnected()

        val purchase = getPurchase(sku) ?: return@awaitAsync false

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.consumePurchase(consumeParams)

        logger.d {
            "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
        }

        val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
        if (success) refreshes.emit(Unit)
        return@awaitAsync success
    }

    override suspend fun acknowledgePurchase(sku: Sku): Boolean = scope.awaitAsync(defaultDispatcher) {
        ensureConnected()
        val purchase = getPurchase(sku) ?: return@awaitAsync false

        if (purchase.isAcknowledged) return@awaitAsync true

        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val result = billingClient.acknowledgePurchase(acknowledgeParams)

        logger.d {
            "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
        }

        val success = result.responseCode == BillingClient.BillingResponseCode.OK
        if (success) refreshes.emit(Unit)
        return@awaitAsync success
    }

    override fun isPurchased(sku: Sku): Flow<Boolean> {
        return merge(
            appForegroundState
                .filter { it == AppForegroundState.FOREGROUND },
            refreshes
        )
            .onStart { emit(Unit) }
            .map { getIsPurchased(sku) }
            .distinctUntilChanged()
            .onEach { logger.d { "is purchased flow for $sku -> $it" } }
    }

    override suspend fun isBillingFeatureSupported(feature: BillingFeature): Boolean =
        withContext(defaultDispatcher) {
            ensureConnected()
            val result = billingClient.isFeatureSupported(feature.value)
            logger.d { "is feature supported $feature ? ${result.responseCode} ${result.debugMessage}" }
            return@withContext result.responseCode == BillingClient.BillingResponseCode.OK
        }

    private suspend fun getIsPurchased(sku: Sku): Boolean = withContext(defaultDispatcher) {
        ensureConnected()
        val purchase = getPurchase(sku) ?: return@withContext false
        val isPurchased = purchase.realPurchaseState == Purchase.PurchaseState.PURCHASED
        logger.d { "get is purchased for $sku result is $isPurchased for $purchase" }
        return@withContext isPurchased
    }

    private suspend fun getPurchase(sku: Sku): Purchase? = withContext(ioDispatcher) {
        ensureConnected()
        billingClient.queryPurchases(sku.type.value)
            .purchasesList
            ?.firstOrNull { it.sku == sku.skuString }
            .also { logger.d { "got purchase $it for $sku" } }
    }

    private suspend fun getSkuDetails(sku: Sku): SkuDetails? = withContext(ioDispatcher) {
        ensureConnected()
        billingClient.querySkuDetails(sku.toSkuDetailsParams())
            .skuDetailsList
            ?.firstOrNull { it.sku == sku.skuString }
            .also { logger.d { "got sku details $it for $sku" } }
    }

    private suspend fun ensureConnected() = withContext(ioDispatcher) {
        if (billingClient.isReady) return@withContext
        if (connecting.compareAndSet(false, true)) return@withContext
        suspendCoroutine<Unit> { continuation ->
            logger.d { "start connection" }
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(result: BillingResult) {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            logger.d { "connected" }
                            continuation.resume(Unit)
                        } else {
                            logger.d { "connecting failed ${result.responseCode} ${result.debugMessage}" }
                        }

                        connecting.set(false)
                    }

                    override fun onBillingServiceDisconnected() {
                        logger.d { "on billing service disconnected" }
                    }
                }
            )
        }
    }

    private val Purchase.realPurchaseState: Int
        get() = JSONObject(originalJson).optInt("purchaseState", 0)
}

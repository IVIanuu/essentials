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

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.InternalPurchasesResult
import com.android.billingclient.api.PriceChangeConfirmationListener
import com.android.billingclient.api.PriceChangeFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.RewardLoadParams
import com.android.billingclient.api.RewardResponseListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.ivianuu.essentials.billing.DebugBillingClient.ClientState.CLOSED
import com.ivianuu.essentials.billing.DebugBillingClient.ClientState.CONNECTED
import com.ivianuu.essentials.billing.DebugBillingClient.ClientState.DISCONNECTED
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.essentials.ui.resource.produceResource
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Given(ApplicationComponent::class)
class DebugBillingClient(
    private val purchasesUpdatedListener: PurchasesUpdatedListener
) : BillingClient() {

    private var billingClientStateListener: BillingClientStateListener? = null

    private enum class ClientState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        CLOSED
    }

    private val requests = ConcurrentHashMap<String, PurchaseRequest>()

    private var clientState = DISCONNECTED

    override fun isReady(): Boolean = clientState == CONNECTED

    override fun startConnection(listener: BillingClientStateListener) {
        if (isReady) {
            listener.onBillingSetupFinished(
                BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build()
            )
            return
        }

        if (clientState == CLOSED) {
            listener.onBillingSetupFinished(BillingResult.newBuilder().setResponseCode(BillingResponseCode.DEVELOPER_ERROR).build())
            return
        }

        this.billingClientStateListener = listener
        clientState = CONNECTED
        listener.onBillingSetupFinished(
            BillingResult.newBuilder().setResponseCode(
                BillingResponseCode.OK
            ).build()
        )
    }

    override fun endConnection() {
        billingClientStateListener?.onBillingServiceDisconnected()
        clientState = CLOSED
    }

    override fun isFeatureSupported(feature: String?): BillingResult {
        return if (!isReady) {
            BillingResult.newBuilder().setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                .build()
        } else {
            BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build()
        }
    }

    override fun consumeAsync(consumeParams: ConsumeParams?, listener: ConsumeResponseListener) {
        val purchaseToken = consumeParams?.purchaseToken
        if (purchaseToken == null || purchaseToken.isBlank()) {
            listener.onConsumeResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.DEVELOPER_ERROR
                ).build(), purchaseToken
            )
            return
        }

        globalScope.launch {
            val purchase = getPurchaseByToken(purchaseToken)
            if (purchase != null) {
                removePurchase(purchase.purchaseToken)
                listener.onConsumeResponse(
                    BillingResult.newBuilder().setResponseCode(
                        BillingResponseCode.OK
                    ).build(), purchaseToken
                )
            } else {
                listener.onConsumeResponse(
                    BillingResult.newBuilder().setResponseCode(
                        BillingResponseCode.ITEM_NOT_OWNED
                    ).build(), purchaseToken
                )
            }
        }
    }

    override fun launchBillingFlow(activity: Activity?, params: BillingFlowParams?): BillingResult {
        if (params == null) return BillingResult.newBuilder()
            .setResponseCode(BillingResponseCode.DEVELOPER_ERROR).build()

        globalScope.launch {
            val requestId = UUID.randomUUID().toString()
            val request = PurchaseRequest(params.sku, params.skuType!!)
            requests[requestId] = request

            startUi()

            navigator.push(
                DialogRoute(
                    onDismiss = {
                        globalScope.launch {
                            onPurchaseResult(
                                requestId = requestId,
                                responseCode = BillingResponseCode.USER_CANCELED,
                                purchases = null
                            )

                            navigator.popTop()
                        }
                    }
                ) {
                    ResourceBox(
                        resource = produceResource(requestId) {
                            getSkuDetailsForRequest(requestId)
                        }
                    ) { skuDetails ->
                        if (skuDetails == null) {
                            navigator.popTop()
                        } else {
                            DebugPurchaseDialog(
                                skuDetails = skuDetails,
                                onPurchaseClick = {
                                    globalScope.launch {
                                        onPurchaseResult(
                                            requestId = requestId,
                                            responseCode = BillingResponseCode.OK,
                                            purchases = listOf(skuDetails.toPurchaseData())
                                        )

                                        navigator.popTop()
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }

        return BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build()
    }

    override fun queryPurchaseHistoryAsync(
        skuType: String?,
        listener: PurchaseHistoryResponseListener
    ) {
        if (!isReady) {
            listener.onPurchaseHistoryResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.SERVICE_DISCONNECTED
                ).build(), null
            )
            return
        }
        globalScope.launch {
            val history = queryPurchases(skuType)
            listener.onPurchaseHistoryResponse(BillingResult.newBuilder()
                .setResponseCode(history.responseCode).build(),
                history.purchasesList.map { PurchaseHistoryRecord(it.originalJson, it.signature) })
        }
    }

    override fun querySkuDetailsAsync(
        params: SkuDetailsParams,
        listener: SkuDetailsResponseListener
    ) {
        if (!isReady) {
            listener.onSkuDetailsResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.SERVICE_DISCONNECTED
                ).build(), null
            )
            return
        }
        globalScope.launch {
            listener.onSkuDetailsResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.OK
                ).build(), getSkuDetails(params)
            )
        }
    }

    override fun queryPurchases(@SkuType skuType: String?): Purchase.PurchasesResult {
        if (!isReady) {
            return InternalPurchasesResult(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.SERVICE_DISCONNECTED
                ).build(), null
            )
        }
        if (skuType == null || skuType.isBlank()) {
            return InternalPurchasesResult(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.DEVELOPER_ERROR
                ).build(), /* purchasesList */ null
            )
        }
        return runBlocking { getPurchases(skuType) }
    }

    override fun launchPriceChangeConfirmationFlow(
        activity: Activity?,
        params: PriceChangeFlowParams?,
        listener: PriceChangeConfirmationListener
    ) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun loadRewardedSku(params: RewardLoadParams?, listener: RewardResponseListener) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun acknowledgePurchase(
        params: AcknowledgePurchaseParams?,
        listener: AcknowledgePurchaseResponseListener?
    ) {
        val purchaseToken = params?.purchaseToken
        if (purchaseToken == null || purchaseToken.isBlank()) {
            listener?.onAcknowledgePurchaseResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.DEVELOPER_ERROR
                ).build()
            )
            return
        }

        globalScope.launch {
            val purchase = getPurchaseByToken(purchaseToken)
            if (purchase != null) {
                val updated = Purchase(
                    orderId = purchase.orderId,
                    packageName = purchase.packageName,
                    sku = purchase.sku,
                    purchaseTime = purchase.purchaseTime,
                    purchaseToken = purchase.purchaseToken,
                    signature = purchase.signature,
                    isAcknowledged = true,
                    isAutoRenewing = purchase.isAutoRenewing,
                    developerPayload = purchase.developerPayload
                )
                addPurchase(updated)
                listener?.onAcknowledgePurchaseResponse(
                    BillingResult.newBuilder().setResponseCode(
                        BillingResponseCode.OK
                    ).build()
                )
            } else {
                listener?.onAcknowledgePurchaseResponse(
                    BillingResult.newBuilder().setResponseCode(
                        BillingResponseCode.ITEM_NOT_OWNED
                    ).build()
                )
            }
        }
    }

    internal suspend fun getSkuDetailsForRequest(requestId: String): SkuDetails? {
        return withContext(dispatchers.default) {
            val request = requests[requestId] ?: return@withContext null
            getSkuDetails(
                SkuDetailsParams.newBuilder()
                    .setType(request.skuType)
                    .setSkusList(listOf(request.sku))
                    .build()
            ).firstOrNull()
        }
    }

    internal suspend fun onPurchaseResult(
        requestId: String,
        responseCode: Int,
        purchases: List<Purchase>?
    ) = withContext(dispatchers.default) {
        requests -= requestId

        if (responseCode == BillingResponseCode.OK) {
            purchases!!.forEach { addPurchase(it) }
        }

        purchasesUpdatedListener.onPurchasesUpdated(
            BillingResult.newBuilder().setResponseCode(
                responseCode
            ).build(), purchases
        )
    }

    private fun SkuDetails.toPurchaseData(): Purchase {
        val json =
            """{"orderId":"$sku..0","packageName":"${given<BuildInfo>().packageName}","productId":
      |"$sku","autoRenewing":true,"purchaseTime":"${Date().time}","acknowledged":false,"purchaseToken":
      |"0987654321", "purchaseState":1}""".trimMargin()
        return Purchase(json, "debug-signature-$sku-$type")
    }

}

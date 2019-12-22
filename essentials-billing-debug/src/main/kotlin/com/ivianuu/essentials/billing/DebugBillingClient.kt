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
import android.content.Intent
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
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.android.billingclient.util.BillingHelper
import com.ivianuu.essentials.billing.DebugBillingClient.ClientState.CLOSED
import com.ivianuu.essentials.billing.DebugBillingClient.ClientState.CONNECTED
import com.ivianuu.essentials.billing.DebugBillingClient.ClientState.DISCONNECTED
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Factory
class DebugBillingClient(
    @Param private val purchasesUpdatedListener: PurchasesUpdatedListener,
    private val billingStore: BillingStore
) : BillingClient() {

    private val resultListener = { intent: Intent ->
        // Receiving the result from local broadcast and triggering a callback on listener.
        @BillingResponseCode
        val responseCode =
            intent.getIntExtra(DebugBillingActivity.RESPONSE_CODE, BillingResponseCode.ERROR)
                ?: BillingResponseCode.ERROR

        var purchases: List<Purchase>? = null
        if (responseCode == BillingResponseCode.OK) {
            val resultData = intent.getBundleExtra(DebugBillingActivity.RESPONSE_BUNDLE)
            purchases = BillingHelper.extractPurchases(resultData)

            // save the purchase
            purchases.forEach { billingStore.addPurchase(it) }
        }

        // save the result
        purchasesUpdatedListener.onPurchasesUpdated(
            BillingResult.newBuilder().setResponseCode(
                responseCode
            ).build(), purchases
        )
    }

    private var billingClientStateListener: BillingClientStateListener? = null

    private enum class ClientState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        CLOSED
    }

    private var clientState = DISCONNECTED

    override fun isReady(): Boolean = clientState == CONNECTED

    override fun startConnection(listener: BillingClientStateListener) {
        if (isReady) {
            listener.onBillingSetupFinished(BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build())
            return
        }

        if (clientState == CLOSED) {
            listener.onBillingSetupFinished(BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.DEVELOPER_ERROR).build())
            return
        }

        Messager.registerListener(resultListener)

        this.billingClientStateListener = listener
        clientState = CONNECTED
        listener.onBillingSetupFinished(
            BillingResult.newBuilder().setResponseCode(
                BillingResponseCode.OK
            ).build()
        )
    }

    override fun endConnection() {
        Messager.unregisterListener(resultListener)
        billingClientStateListener?.onBillingServiceDisconnected()
        clientState = CLOSED
    }

    override fun isFeatureSupported(feature: String?): BillingResult {
        // TODO Update BillingStore to allow feature enable/disable
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

        GlobalScope.launch {
            val purchase = billingStore.getPurchaseByToken(purchaseToken)
            if (purchase != null) {
                billingStore.removePurchase(purchase.purchaseToken)
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
        val intent = Intent(activity, DebugBillingActivity::class.java)
        intent.putExtra(DebugBillingActivity.REQUEST_SKU_TYPE, params?.skuType)
        intent.putExtra(DebugBillingActivity.REQUEST_SKU, params?.sku)
        activity!!.startActivity(intent)
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
        GlobalScope.launch {
            val history = queryPurchases(skuType)
            listener.onPurchaseHistoryResponse(BillingResult.newBuilder().setResponseCode(history.responseCode).build(),
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
        GlobalScope.launch {
            listener.onSkuDetailsResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.OK
                ).build(), billingStore.getSkuDetails(params)
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
        return billingStore.getPurchases(skuType)
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

        GlobalScope.launch {
            val purchase = billingStore.getPurchaseByToken(purchaseToken)
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
                billingStore.addPurchase(updated)
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
}

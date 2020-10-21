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

package com.ivianuu.essentials.billing.debug

import android.app.Activity
import com.android.billingclient.api.*
import com.ivianuu.essentials.billing.debug.DebugBillingClient.ClientState.*
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.essentials.ui.resource.produceResource
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.startUi
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

@Binding
class DebugBillingClient(
    private val billingStore: BillingStore,
    private val buildInfo: BuildInfo,
    private val defaultDispatcher: DefaultDispatcher,
    private val globalScope: GlobalScope,
    private val startUi: startUi,
    private val navigator: Navigator,
    private val purchasesUpdatedListener: @Assisted PurchasesUpdatedListener
) : BillingClient() {

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
                ).build(),
                purchaseToken
            )
            return
        }

        globalScope.launch {
            val purchase = billingStore.getPurchaseByToken(purchaseToken)
            if (purchase != null) {
                billingStore.removePurchase(purchase.purchaseToken)
                listener.onConsumeResponse(
                    BillingResult.newBuilder().setResponseCode(
                        BillingResponseCode.OK
                    ).build(),
                    purchaseToken
                )
            } else {
                listener.onConsumeResponse(
                    BillingResult.newBuilder().setResponseCode(
                        BillingResponseCode.ITEM_NOT_OWNED
                    ).build(),
                    purchaseToken
                )
            }
        }
    }

    override fun launchBillingFlow(activity: Activity?, params: BillingFlowParams?): BillingResult {
        if (params == null) return BillingResult.newBuilder()
            .setResponseCode(BillingResponseCode.DEVELOPER_ERROR).build()

        globalScope.launch {
            startUi()

            navigator.push(
                DialogRoute(
                    onDismiss = {
                        globalScope.launch {
                            purchasesUpdatedListener.onPurchasesUpdated(
                                BillingResult.newBuilder().setResponseCode(
                                    BillingResponseCode.USER_CANCELED
                                ).build(),
                                null
                            )

                            navigator.popTop()
                        }
                    }
                ) {
                    ResourceBox(
                        resource = produceResource {
                            withContext(defaultDispatcher) {
                                billingStore.getSkuDetails(
                                    SkuDetailsParams.newBuilder()
                                        .setType(params.skuType)
                                        .setSkusList(listOf(params.sku))
                                        .build()
                                ).firstOrNull()
                            }
                        }
                    ) { skuDetails ->
                        if (skuDetails == null) {
                            navigator.popTop()
                        } else {
                            DebugPurchaseDialog(
                                skuDetails = skuDetails,
                                onPurchaseClick = {
                                    globalScope.launch {
                                        val purchases = listOf(skuDetails.toPurchaseData())
                                        purchases.forEach {
                                            billingStore.addPurchase(it)
                                        }

                                        purchasesUpdatedListener.onPurchasesUpdated(
                                            BillingResult.newBuilder().setResponseCode(
                                                BillingResponseCode.OK
                                            ).build(),
                                            purchases
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
                ).build(),
                null
            )
            return
        }
        globalScope.launch {
            val history = queryPurchases(skuType)
            listener.onPurchaseHistoryResponse(
                BillingResult.newBuilder()
                    .setResponseCode(history.responseCode).build(),
                history.purchasesList.map { PurchaseHistoryRecord(it.originalJson, it.signature) }
            )
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
                ).build(),
                null
            )
            return
        }
        globalScope.launch {
            listener.onSkuDetailsResponse(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.OK
                ).build(),
                billingStore.getSkuDetails(params)
            )
        }
    }

    override fun queryPurchases(@SkuType skuType: String?): Purchase.PurchasesResult {
        if (!isReady) {
            return InternalPurchasesResult(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.SERVICE_DISCONNECTED
                ).build(),
                null
            )
        }
        if (skuType == null || skuType.isBlank()) {
            return InternalPurchasesResult(
                BillingResult.newBuilder().setResponseCode(
                    BillingResponseCode.DEVELOPER_ERROR
                ).build(),
                null
            )
        }
        return runBlocking {
            billingStore.getPurchases(
                skuType
            )
        }
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
            val purchase = billingStore.getPurchaseByToken(
                purchaseToken
            )
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

    private fun SkuDetails.toPurchaseData(): Purchase {
        val json =
            """{"orderId":"$sku","packageName":"${buildInfo.packageName}","productId":
      |"$sku","autoRenewing":true,"purchaseTime":"${Date().time}","acknowledged":false,"purchaseToken":
      |"0987654321", "purchaseState":1}""".trimMargin()
        return Purchase(json, "debug-signature-$sku-$type")
    }
}

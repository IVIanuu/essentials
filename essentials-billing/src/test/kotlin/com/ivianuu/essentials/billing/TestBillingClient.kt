/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageResponseListener
import com.android.billingclient.api.PriceChangeConfirmationListener
import com.android.billingclient.api.PriceChangeFlowParams
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestBillingClient(
  @Inject private val scope: CoroutineScope,
  private val purchasesUpdated: () -> Unit
) : BillingClient() {
  var purchases = emptyList<Purchase>()
    set(value) {
      field = value
      purchasesUpdated()
    }
  var skus = emptyList<SkuDetails>()

  override fun isFeatureSupported(feature: String): BillingResult = successResult()

  override fun isReady(): Boolean = isConnected

  private var isConnected = false

  override fun startConnection(listener: BillingClientStateListener) {
    if (isConnected) {
      listener.onBillingSetupFinished(successResult())
      return
    }
    scope.launch {
      delay(10)
      listener.onBillingSetupFinished(successResult())
      isConnected = true
    }
  }

  override fun endConnection() {
    isConnected = false
  }

  override fun getConnectionState(): Int = if (isConnected)
    ConnectionState.CONNECTED else ConnectionState.DISCONNECTED

  override fun launchBillingFlow(activity: Activity, params: BillingFlowParams): BillingResult {
    scope.launch {
      delay(10)
      purchases = purchases + Purchase(sku = params.zze().first().cast<SkuDetails>().sku)
    }
    return successResult()
  }

  override fun launchPriceChangeConfirmationFlow(
    activity: Activity,
    params: PriceChangeFlowParams,
    listener: PriceChangeConfirmationListener
  ) {
    TODO()
  }

  override fun queryPurchasesAsync(
    params: QueryPurchasesParams,
    listener: PurchasesResponseListener
  ) {
    listener.onQueryPurchasesResponse(
      successResult(),
      purchases
    )
  }

  override fun queryPurchasesAsync(params: String, listener: PurchasesResponseListener) {
    TODO()
  }

  override fun queryProductDetailsAsync(
    params: QueryProductDetailsParams,
    listener: ProductDetailsResponseListener
  ) {
    TODO()
  }

  override fun querySkuDetailsAsync(
    params: SkuDetailsParams,
    listener: SkuDetailsResponseListener
  ) {
    scope.launch {
      delay(10)
      listener.onSkuDetailsResponse(
        successResult(),
        skus.filter { it.sku in params.skusList }
      )
    }
  }

  override fun consumeAsync(params: ConsumeParams, listener: ConsumeResponseListener) {
    purchases = purchases.filterNot { it.purchaseToken == params.purchaseToken }
    scope.launch {
      delay(10)
      listener.onConsumeResponse(successResult(), params.purchaseToken)
    }
  }

  override fun queryPurchaseHistoryAsync(
    skuType: String,
    listener: PurchaseHistoryResponseListener
  ) {
    TODO()
  }

  override fun queryPurchaseHistoryAsync(
    params: QueryPurchaseHistoryParams,
    listener: PurchaseHistoryResponseListener
  ) {
    TODO()
  }

  override fun showInAppMessages(
    activity: Activity,
    params: InAppMessageParams,
    listener: InAppMessageResponseListener
  ): BillingResult = TODO()

  override fun acknowledgePurchase(
    params: AcknowledgePurchaseParams,
    listener: AcknowledgePurchaseResponseListener
  ) {
    purchases = purchases
      .map {
        Purchase(
          it.orderId,
          it.packageName,
          it.products.single(),
          it.purchaseTime,
          it.purchaseToken,
          it.signature,
          true,
          it.isAutoRenewing,
          it.developerPayload
        )
      }
    scope.launch {
      delay(10)
      listener.onAcknowledgePurchaseResponse(successResult())
    }
  }

  private fun successResult() = BillingResult
    .newBuilder()
    .setResponseCode(BillingResponseCode.OK)
    .build()
}

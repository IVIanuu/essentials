/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import android.app.*
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

class TestBillingClient(
  private val purchasesUpdated: () -> Unit,
  @Provide private val scope: CoroutineScope
) : BillingClient() {
  var purchases = emptyList<Purchase>()
    set(value) {
      field = value
      purchasesUpdated()
    }
  var skus = emptyList<SkuDetails>()

  override fun isFeatureSupported(p0: String): BillingResult = successResult()

  override fun isReady(): Boolean = isConnected

  private var isConnected = false

  override fun startConnection(listener: BillingClientStateListener) {
    if (isConnected) {
      listener.onBillingSetupFinished(successResult())
      return
    }
    launch {
      delay(10)
      listener.onBillingSetupFinished(successResult())
      isConnected = true
    }
  }

  override fun endConnection() {
    isConnected = false
  }

  override fun launchBillingFlow(activity: Activity, params: BillingFlowParams): BillingResult {
    launch {
      delay(10)
      purchases = purchases + Purchase(sku = params.sku)
    }
    return successResult()
  }

  override fun launchPriceChangeConfirmationFlow(
    p0: Activity,
    p1: PriceChangeFlowParams,
    p2: PriceChangeConfirmationListener
  ) {
    TODO()
  }

  override fun queryPurchases(sku: String): Purchase.PurchasesResult = Purchase.PurchasesResult(
    successResult(),
    purchases
  )

  override fun querySkuDetailsAsync(
    params: SkuDetailsParams,
    listener: SkuDetailsResponseListener
  ) {
    launch {
      delay(10)
      listener.onSkuDetailsResponse(
        successResult(),
        skus.filter { it.sku in params.skusList }
      )
    }
  }

  override fun consumeAsync(params: ConsumeParams, listener: ConsumeResponseListener) {
    purchases = purchases.filterNot { it.purchaseToken == params.purchaseToken }
    launch {
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

  override fun acknowledgePurchase(
    params: AcknowledgePurchaseParams,
    listener: AcknowledgePurchaseResponseListener
  ) {
    purchases = purchases
      .map {
        Purchase(
          it.orderId,
          it.packageName,
          it.sku,
          it.purchaseTime,
          it.purchaseToken,
          it.signature,
          true,
          it.isAutoRenewing,
          it.developerPayload
        )
      }
    launch {
      delay(10)
      listener.onAcknowledgePurchaseResponse(successResult())
    }
  }

  private fun successResult() = BillingResult
    .newBuilder()
    .setResponseCode(BillingResponseCode.OK)
    .build()
}

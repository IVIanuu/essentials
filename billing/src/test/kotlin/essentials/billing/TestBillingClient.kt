/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.billing

import android.app.*
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMap
import com.android.billingclient.api.*
import essentials.*
import kotlinx.coroutines.*

class TestBillingClient(
  private val scope: CoroutineScope,
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
      purchases = purchases + Purchase(sku = params.zze()!!.first().cast<SkuDetails>().sku)
    }
    return successResult()
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
        skus.fastFilter { it.sku in params.skusList }
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
      .fastMap {
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

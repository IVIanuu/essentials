/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryPurchasesAsync
import com.android.billingclient.api.querySkuDetails
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.IOContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface BillingService {
  fun isPurchased(sku: Sku): Flow<Boolean>

  suspend fun getSkuDetails(sku: Sku): SkuDetails?

  suspend fun purchase(
    sku: Sku,
    acknowledge: Boolean = true,
    consumeOldPurchaseIfUnspecified: Boolean = true
  ): Boolean

  suspend fun consumePurchase(sku: Sku): Boolean

  suspend fun acknowledgePurchase(sku: Sku): Boolean
}

context(Logger, NamedCoroutineScope<AppScope>) @Provide @Scoped<AppScope> class BillingServiceImpl(
  private val appForegroundState: Flow<AppForegroundState>,
  private val appUiStarter: AppUiStarter,
  private val billingClient: BillingClient,
  private val context: IOContext,
  private val refreshes: MutableSharedFlow<BillingRefresh>
) : BillingService {
  private var isConnected = false
  private val connectionLock = Mutex()

  override fun isPurchased(sku: Sku): Flow<Boolean> = merge(
    appForegroundState
      .filter { it == AppForegroundState.FOREGROUND },
    refreshes
  )
    .onStart { emit(Unit) }
    .map { withConnection { getIsPurchased(sku) } ?: false }
    .distinctUntilChanged()
    .onEach { log { "is purchased flow for $sku -> $it" } }

  override suspend fun getSkuDetails(sku: Sku): SkuDetails? = withConnection {
    billingClient.querySkuDetails(sku.toSkuDetailsParams())
      .skuDetailsList
      ?.firstOrNull { it.sku == sku.skuString }
      .also { log { "got sku details $it for $sku" } }
  }

  override suspend fun purchase(
    sku: Sku,
    acknowledge: Boolean,
    consumeOldPurchaseIfUnspecified: Boolean
  ): Boolean = withConnection {
    log {
      "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified"
    }
    if (consumeOldPurchaseIfUnspecified) {
      val oldPurchase = getPurchase(sku)
      if (oldPurchase != null) {
        if (oldPurchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
          consumePurchase(sku)
        }
      }
    }

    val activity = appUiStarter()

    val skuDetails = getSkuDetails(sku)
      ?: return@withConnection false

    val billingFlowParams = BillingFlowParams.newBuilder()
      .setSkuDetails(skuDetails)
      .build()

    val result = billingClient.launchBillingFlow(activity, billingFlowParams)
    if (result.responseCode != BillingClient.BillingResponseCode.OK)
      return@withConnection false

    refreshes.first()

    val success = getIsPurchased(sku)

    return@withConnection if (success && acknowledge) acknowledgePurchase(sku) else success
  } ?: false

  override suspend fun consumePurchase(sku: Sku): Boolean = withConnection {
    val purchase = getPurchase(sku) ?: return@withConnection false

    val consumeParams = ConsumeParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.consumePurchase(consumeParams)

    log {
      "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
    }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@withConnection success
  } ?: false

  override suspend fun acknowledgePurchase(sku: Sku): Boolean = withConnection {
    val purchase = getPurchase(sku)
      ?: return@withConnection false

    if (purchase.isAcknowledged) return@withConnection true

    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.acknowledgePurchase(acknowledgeParams)

    log {
      "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
    }

    val success = result.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@withConnection success
  } ?: false

  context(BillingService) private suspend fun getIsPurchased(sku: Sku): Boolean {
    val purchase = getPurchase(sku) ?: return false
    val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
    log { "get is purchased for $sku result is $isPurchased for $purchase" }
    return isPurchased
  }

  context(BillingService) private suspend fun getPurchase(sku: Sku): Purchase? =
    billingClient.queryPurchasesAsync(
      QueryPurchasesParams.newBuilder()
        .setProductType(sku.type.value)
        .build()
    )
      .purchasesList
      .firstOrNull { sku.skuString in it.skus }
      .also { log { "got purchase $it for $sku" } }

  internal suspend fun <R> withConnection(block: suspend context(BillingService) () -> R): R? =
    withContext(coroutineContext + context) {
      ensureConnected()
      block()
    }

  private suspend fun ensureConnected(): Unit = connectionLock.withLock {
    if (isConnected) return@withLock
    suspendCoroutine<Unit?> { continuation ->
      log { "start connection" }
      billingClient.startConnection(
        object : BillingClientStateListener {
          private var completed = false
          override fun onBillingSetupFinished(result: BillingResult) {
            // for some reason on billing setup finished is sometimes called multiple times
            // we ensure that we we only resume once
            if (!completed) {
              completed = true
              if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                log { "connected" }
                isConnected = true
                continuation.resume(Unit)
              } else {
                log { "connecting failed ${result.responseCode} ${result.debugMessage}" }
                continuation.resume(null)
              }
            }
          }

          override fun onBillingServiceDisconnected() {
            log { "on billing service disconnected" }
          }
        }
      )
    }
  }
}

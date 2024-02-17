/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Provide @Scoped<AppScope> class BillingService(
  private val appUiStarter: AppUiStarter,
  private val billingClientFactory: () -> BillingClient,
  coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val refreshes: MutableSharedFlow<BillingRefresh>,
  scope: Scope<AppScope>,
  private val scopeManager: ScopeManager
) {
  private val billingClient = scope.coroutineScope.childCoroutineScope(coroutineContexts.io).sharedResource(
    sharingStarted = SharingStarted.WhileSubscribed(10.seconds.inWholeMilliseconds),
    create = { _: Unit ->
      logger.log { "create client" }
      val client = billingClientFactory()
      suspendCancellableCoroutine { continuation ->
        client.startConnection(
          object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
              // for some reason on billing setup finished is sometimes called multiple times
              // we ensure that we we only resume once
              if (continuation.isActive) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                  logger.log { "connected" }
                  continuation.resume(Unit)
                } else {
                  continuation.resumeWithException(
                    IllegalStateException("connecting failed ${result.responseCode} ${result.debugMessage}")
                  )
                }
              }
            }

            override fun onBillingServiceDisconnected() {
              logger.log { "on billing service disconnected" }
            }
          }
        )
      }
      client
        .also { logger.log { "client created" } }
     },
    release = { _, billingClient ->
      logger.log { "release client" }
      catch { billingClient.endConnection() }
    }
  )

  fun isPurchased(sku: Sku): Flow<Boolean> = scopeManager.flowInScope<AppVisibleScope, _>(
    refreshes.onStart { emit(BillingRefresh) }
      .onStart { emit(BillingRefresh) }
      .onEach { logger.log { "update is purchased for $sku" } }
      .map { billingClient.use(Unit) { it.getIsPurchased(sku) } }
      .distinctUntilChanged()
      .onEach { logger.log { "is purchased for $sku -> $it" } }
  )

  suspend fun getSkuDetails(sku: Sku): SkuDetails? = billingClient.use(Unit) {
    it.querySkuDetails(sku.toSkuDetailsParams())
      .skuDetailsList
      ?.firstOrNull { it.sku == sku.skuString }
      .also { logger.log { "got sku details $it for $sku" } }
  }

  suspend fun purchase(
    sku: Sku,
    acknowledge: Boolean,
    consumeOldPurchaseIfUnspecified: Boolean
  ): Boolean = billingClient.use(Unit) { billingClient ->
    logger.log {
      "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified"
    }
    if (consumeOldPurchaseIfUnspecified) {
      val oldPurchase = billingClient.getPurchase(sku)
      if (oldPurchase != null) {
        if (oldPurchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
          consumePurchase(sku)
        }
      }
    }

    val activity = appUiStarter()

    val skuDetails = getSkuDetails(sku)
      ?: return@use false

    val billingFlowParams = BillingFlowParams.newBuilder()
      .setSkuDetails(skuDetails)
      .build()

    val result = billingClient.launchBillingFlow(activity, billingFlowParams)
    if (result.responseCode != BillingClient.BillingResponseCode.OK)
      return@use false

    refreshes.first()

    val success = billingClient.getIsPurchased(sku)

    return@use if (success && acknowledge) acknowledgePurchase(sku) else success
  }

  suspend fun consumePurchase(sku: Sku): Boolean = billingClient.use(Unit) { billingClient ->
    val purchase = billingClient.getPurchase(sku) ?: return@use false

    val consumeParams = ConsumeParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.consumePurchase(consumeParams)

    logger.log {
      "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
    }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@use success
  }

  suspend fun acknowledgePurchase(sku: Sku): Boolean = billingClient.use(Unit) { billingClient ->
    val purchase = billingClient.getPurchase(sku)
      ?: return@use false

    if (purchase.isAcknowledged) return@use true

    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.acknowledgePurchase(acknowledgeParams)

    logger.log {
      "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
    }

    val success = result.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@use success
  }

  private suspend fun BillingClient.getIsPurchased(sku: Sku): Boolean {
    val purchase = getPurchase(sku) ?: return false
    val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
    logger.log { "get is purchased for $sku result is $isPurchased for $purchase" }
    return isPurchased
  }

  private suspend fun BillingClient.getPurchase(sku: Sku): Purchase? =
    queryPurchasesAsync(
      QueryPurchasesParams.newBuilder()
        .setProductType(sku.type.value)
        .build()
    )
      .purchasesList
      .firstOrNull { sku.skuString in it.skus }
      .also { logger.log { "got purchase $it for $sku" } }
}

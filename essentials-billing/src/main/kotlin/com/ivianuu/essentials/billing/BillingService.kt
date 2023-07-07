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
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.RefCountedResource
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.childCoroutineScope
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.coroutines.withResource
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

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

@Provide @Scoped<AppScope> class BillingServiceImpl(
  private val appUiStarter: AppUiStarter,
  private val appForegroundState: Flow<AppForegroundState>,
  private val billingClientFactory: () -> BillingClient,
  coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val refreshes: MutableSharedFlow<BillingRefresh>,
  scope: ScopedCoroutineScope<AppScope>
) : BillingService {
  private val billingClient = RefCountedResource<Unit, BillingClient>(
    timeout = 10.seconds,
    create = {
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
    },
    scope = scope.childCoroutineScope(coroutineContexts.io)
  )

  override fun isPurchased(sku: Sku): Flow<Boolean> = appForegroundState
    .flatMapLatest {
      if (it == AppForegroundState.BACKGROUND) infiniteEmptyFlow()
      else refreshes.onStart { emit(BillingRefresh) }
    }
    .onStart { emit(BillingRefresh) }
    .onEach { logger.log { "update is purchased for $sku" } }
    .map { withBillingClient { getIsPurchased(sku) } ?: false }
    .distinctUntilChanged()
    .onEach { logger.log { "is purchased for $sku -> $it" } }

  override suspend fun getSkuDetails(sku: Sku): SkuDetails? = withBillingClient {
    querySkuDetails(sku.toSkuDetailsParams())
      .skuDetailsList
      ?.firstOrNull { it.sku == sku.skuString }
      .also { logger.log { "got sku details $it for $sku" } }
  }

  override suspend fun purchase(
    sku: Sku,
    acknowledge: Boolean,
    consumeOldPurchaseIfUnspecified: Boolean
  ): Boolean = withBillingClient {
    logger.log {
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
      ?: return@withBillingClient false

    val billingFlowParams = BillingFlowParams.newBuilder()
      .setSkuDetails(skuDetails)
      .build()

    val result = launchBillingFlow(activity, billingFlowParams)
    if (result.responseCode != BillingClient.BillingResponseCode.OK)
      return@withBillingClient false

    refreshes.first()

    val success = getIsPurchased(sku)

    return@withBillingClient if (success && acknowledge) acknowledgePurchase(sku) else success
  } ?: false

  override suspend fun consumePurchase(sku: Sku): Boolean = withBillingClient {
    val purchase = getPurchase(sku) ?: return@withBillingClient false

    val consumeParams = ConsumeParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = consumePurchase(consumeParams)

    logger.log {
      "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
    }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@withBillingClient success
  } ?: false

  override suspend fun acknowledgePurchase(sku: Sku): Boolean = withBillingClient {
    val purchase = getPurchase(sku)
      ?: return@withBillingClient false

    if (purchase.isAcknowledged) return@withBillingClient true

    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = acknowledgePurchase(acknowledgeParams)

    logger.log {
      "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
    }

    val success = result.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@withBillingClient success
  } ?: false

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

  internal suspend fun <R> withBillingClient(block: suspend BillingClient.() -> R): R? =
    billingClient.withResource(Unit) { block(it) }
}

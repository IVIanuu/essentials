/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.billing

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import com.android.billingclient.api.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import essentials.logging.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.seconds

@Stable @Provide @Scoped<AppScope> class Billing(
  @property:Provide private val appScope: Scope<AppScope>,
  private val launchUi: launchUi,
  private val billingClientFactory: () -> BillingClient,
  coroutineContexts: CoroutineContexts,
  @property:Provide private val logger: Logger,
  private val refreshes: MutableSharedFlow<BillingRefresh>
) {
  private val client = provide(implicitly<CoroutineScope>().childCoroutineScope(coroutineContexts.io)) {
    sharedResource(
      sharingStarted = SharingStarted.WhileSubscribed(10.seconds.inWholeMilliseconds),
      create = { _: Unit ->
        d { "create client" }
        val client = billingClientFactory()
        suspendCancellableCoroutine { continuation ->
          client.startConnection(
            object : BillingClientStateListener {
              override fun onBillingSetupFinished(result: BillingResult) {
                // for some reason on billing setup finished is sometimes called multiple times
                // we ensure that we we only resume once
                if (continuation.isActive) {
                  if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    d { "connected" }
                    continuation.resume(Unit)
                  } else {
                    continuation.resumeWithException(
                      IllegalStateException("connecting failed ${result.responseCode} ${result.debugMessage}")
                    )
                  }
                }
              }

              override fun onBillingServiceDisconnected() {
                d { "on billing service disconnected" }
              }
            }
          )
        }
        client
          .also { d { "client created" } }
      },
      release = { _, billingClient ->
        d { "release client" }
        catch { billingClient.endConnection() }
      }
    )
  }

  @Composable fun isPurchased(sku: Sku): Boolean? {
    var isPurchased: Boolean? by remember { mutableStateOf(null) }

    if (appScope.scopeOfOrNull<AppVisibleScope>() != null) {
      val version by produceState(0) { refreshes.collect { value += 1 } }

      LaunchedEffect(version) {
        isPurchased = client.use(Unit) { it.getIsPurchased(sku) }
        d { "is purchased for $sku -> $isPurchased" }
      }
    }

    return isPurchased
  }

  suspend fun getSkuDetails(sku: Sku): SkuDetails? = client.use(Unit) {
    it.querySkuDetails(sku.toSkuDetailsParams())
      .skuDetailsList
      ?.fastFirstOrNull { it.sku == sku.skuString }
      .also { d { "got sku details $it for $sku" } }
  }

  suspend fun purchase(
    sku: Sku,
    acknowledge: Boolean,
    consumeOldPurchaseIfUnspecified: Boolean
  ): Boolean = client.use(Unit) { billingClient ->
    d {
      "purchase $sku -> acknowledge $acknowledge, consume old $consumeOldPurchaseIfUnspecified"
    }
    if (consumeOldPurchaseIfUnspecified) {
      val oldPurchase = billingClient.getPurchase(sku)
      if (oldPurchase != null && oldPurchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE)
        consumePurchase(sku)
    }

    val activity = launchUi().activity

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

  suspend fun consumePurchase(sku: Sku): Boolean = client.use(Unit) { billingClient ->
    val purchase = billingClient.getPurchase(sku) ?: return@use false

    val consumeParams = ConsumeParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.consumePurchase(consumeParams)

    d {
      "consume purchase $sku result ${result.billingResult.responseCode} ${result.billingResult.debugMessage}"
    }

    val success = result.billingResult.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@use success
  }

  suspend fun acknowledgePurchase(sku: Sku): Boolean = client.use(Unit) { billingClient ->
    val purchase = billingClient.getPurchase(sku)
      ?: return@use false

    if (purchase.isAcknowledged) return@use true

    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
      .setPurchaseToken(purchase.purchaseToken)
      .build()

    val result = billingClient.acknowledgePurchase(acknowledgeParams)

    d {
      "acknowledge purchase $sku result ${result.responseCode} ${result.debugMessage}"
    }

    val success = result.responseCode == BillingClient.BillingResponseCode.OK
    if (success) refreshes.emit(BillingRefresh)
    return@use success
  }

  private suspend fun BillingClient.getIsPurchased(sku: Sku): Boolean {
    val purchase = getPurchase(sku) ?: return false
    val isPurchased = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
    d { "get is purchased for $sku result is $isPurchased for $purchase" }
    return isPurchased
  }

  private suspend fun BillingClient.getPurchase(sku: Sku): Purchase? =
    queryPurchasesAsync(
      QueryPurchasesParams.newBuilder()
        .setProductType(sku.type.value)
        .build()
    )
      .purchasesList
      .fastFirstOrNull { sku.skuString in it.skus }
      .also { d { "got purchase $it for $sku" } }
}

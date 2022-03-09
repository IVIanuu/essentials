/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.*

val TestSku = Sku("sku", Sku.Type.IN_APP)

fun TestBillingClient.withTestSku(): TestBillingClient = apply {
  skus += SkuDetails(TestSku)
}

class TestBillingContext(scope: CoroutineScope) : BillingContext {
  override val billingClient = TestBillingClient(
    { refreshes.tryEmit(BillingRefresh) },
    scope
  )
  override val refreshes: MutableSharedFlow<BillingRefresh> = EventFlow()
  @Provide override val logger: Logger = PrintingLogger(LoggingEnabled(true))
  override suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R = block()
}

fun Purchase(
  orderId: String? = null,
  packageName: String? = null,
  sku: String,
  purchaseTime: Long = 831852000000,
  purchaseToken: String = "dummy_token",
  signature: String = "dummy_signature",
  isAcknowledged: Boolean? = null,
  isAutoRenewing: Boolean? = null,
  developerPayload: String? = null
): Purchase {
  val json = JSONObject()
  orderId?.let { json.put("orderId", it) }
  packageName?.let { json.put("packageName", it) }
  json.put("productId", sku)
  json.put("purchaseTime", purchaseTime)
  json.put("purchaseToken", purchaseToken)
  isAcknowledged?.let { json.put("acknowledged", it) }
  isAutoRenewing?.let { json.put("autoRenewing", it) }
  developerPayload?.let { json.put("developerPayload", it) }
  return Purchase(json.toString(), signature)
}

fun SkuDetails(
  sku: Sku,
  price: String = "0.99$",
  priceAmountMicros: Long = 3990000,
  priceCurrencyCode: String = "$",
  title: String = "Dummy title",
  description: String = "Dummy description",
  subscriptionPeriod: String? = null,
  freeTrialPeriod: String? = null,
  introductoryPrice: String? = null,
  introductoryPriceAmountMicros: String? = null,
  introductoryPricePeriod: String? = null,
  introductoryPriceCycles: String? = null
): SkuDetails {
  val json = JSONObject()
    .put("productId", sku.skuString)
    .put("type", sku.type.value)
    .put("price", price)
    .put("price_amount_micros", priceAmountMicros)
    .put("price_currency_code", priceCurrencyCode)
    .put("title", title)
    .put("description", description)

  subscriptionPeriod?.let { json.put("subscriptionPeriod", subscriptionPeriod) }
  freeTrialPeriod?.let { json.put("freeTrialPeriod", freeTrialPeriod) }
  introductoryPrice?.let { json.put("introductoryPrice", introductoryPrice) }
  introductoryPriceAmountMicros?.let {
    json.put(
      "introductoryPriceAmountMicros",
      introductoryPriceAmountMicros
    )
  }
  introductoryPricePeriod?.let {
    json.put(
      "introductoryPricePeriod",
      introductoryPricePeriod
    )
  }
  introductoryPriceCycles?.let {
    json.put(
      "introductoryPriceCycles",
      introductoryPriceCycles
    )
  }

  return SkuDetails(json.toString())
}

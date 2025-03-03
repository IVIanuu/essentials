/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import com.android.billingclient.api.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.donation.*
import injekt.*
import org.json.*

@Provide val donationHomeItem = HomeItem("Donation") { DonationScreen() }

private fun SkuDetails(
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

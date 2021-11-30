/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.billing.ConsumePurchaseUseCase
import com.ivianuu.essentials.billing.GetSkuDetailsUseCase
import com.ivianuu.essentials.billing.PurchaseUseCase
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.donation.Donation
import com.ivianuu.essentials.donation.DonationKey
import com.ivianuu.essentials.sample.R
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import org.json.JSONObject

@Provide val donationHomeItem = HomeItem("Donation") { DonationKey }

@Provide val sampleDonations = listOf(
  Donation(Sku("crossaint", Sku.Type.IN_APP), R.drawable.es_ic_bakery_dining),
  Donation(Sku("coffee", Sku.Type.IN_APP), R.drawable.es_ic_free_breakfast),
  Donation(Sku("burger_menu", Sku.Type.IN_APP), R.drawable.es_ic_lunch_dining),
  Donation(Sku("movie", Sku.Type.IN_APP), R.drawable.es_ic_popcorn)
)

@Provide val sampleGetSkuDetailsUseCase = GetSkuDetailsUseCase {
  delay(2000)
  when (it.skuString) {
    "crossaint" -> SkuDetails(sku = it, title = "A crossaint", price = "0.99€")
    "coffee" -> SkuDetails(sku = it, title = "A cup of coffee", price = "2.49€")
    "burger_menu" -> SkuDetails(sku = it, title = "Burger menu", price = "4.99€")
    "movie" -> SkuDetails(sku = it, title = "Movie with popcorn", price = "9.99€")
    else -> throw AssertionError()
  }
}

@Provide val samplePurchaseUseCase = PurchaseUseCase { _, _, _ ->
  delay(3000)
  true
}

@Provide val sampleConsumePurchaseUseCase = ConsumePurchaseUseCase { true }

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
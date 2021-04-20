package com.ivianuu.essentials.sample.ui

import com.android.billingclient.api.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.donation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import org.json.*

@Given
val donationHomeItem = HomeItem("Donation") { DonationKey(listOf(DonationSku)) }

val DonationSku = Sku("donate")

@Given
val sampleGetSkuDetailsUseCase: GetSkuDetailsUseCase = {
    delay(2000)
    SkuDetails(it)
}

@Given
val samplePurchaseUseCase: PurchaseUseCase = { _, _, _ ->
    delay(3000)
    true
}

@Given
val sampleConsumePurchaseUseCase: ConsumePurchaseUseCase = { true }

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
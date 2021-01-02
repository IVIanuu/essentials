/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.billing.debug

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.datastore.android.prefBinding
import com.ivianuu.essentials.moshi.JsonAdapterBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class DebugBillingPrefs(
    @Json(name = "billing_products") val products: List<SkuDetails> = emptyList(),
    @Json(name = "billing_purchases") val purchases: List<Purchase> = emptyList(),
)

@Module val debugBillingPrefsBinding =
    prefBinding<DebugBillingPrefs>("debug_billing_prefs")

@JsonAdapterBinding @Given
class PurchaseAdapter {
    @ToJson fun toJson(purchase: Purchase) = "${purchase.originalJson}=:=${purchase.signature}"
    @FromJson fun fromJson(value: String): Purchase {
        val tmp = value.split("=:=")
        return Purchase(tmp[0], tmp[1])
    }
}

@JsonAdapterBinding @Given
class SkuDetailsAdapter {
    @ToJson fun toJson(skuDetails: SkuDetails) = skuDetails.originalJson
    @FromJson fun fromJson(value: String): SkuDetails = SkuDetails(value)
}

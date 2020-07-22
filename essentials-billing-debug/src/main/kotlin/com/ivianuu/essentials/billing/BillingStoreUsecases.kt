/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.InternalPurchasesResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Reader
suspend fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails> =
    withContext(dispatchers.default) {
        given<BillingPrefs>().products.data.first()
            .filter { it.sku in params.skusList && it.type == params.skuType }
    }

@Reader
suspend fun addProduct(skuDetails: SkuDetails): Unit = withContext(dispatchers.default) {
    given<BillingPrefs>().products.updateData { it + skuDetails }
}

@Reader
suspend fun removeProduct(sku: String): Unit = withContext(dispatchers.default) {
    given<BillingPrefs>().products.updateData { products ->
        products.filter { it.sku != sku }
    }
}

@Reader
suspend fun clearProducts(): Unit = withContext(dispatchers.default) {
    given<BillingPrefs>().products.updateData { emptyList() }
}

@Reader
suspend fun getPurchases(@SkuType skuType: String): PurchasesResult =
    withContext(dispatchers.default) {
        InternalPurchasesResult(
            BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK).build(),
            given<BillingPrefs>().purchases.data.first().filter { it.signature.endsWith(skuType) })
    }.also {
        d { "got purchase result for $skuType -> ${it.responseCode} ${it.purchasesList}" }
    }

@Reader
suspend fun getPurchaseByToken(purchaseToken: String): Purchase? =
    withContext(dispatchers.default) {
        given<BillingPrefs>().purchases.data.first()
            .firstOrNull { it.purchaseToken == purchaseToken }
    }

@Reader
suspend fun addPurchase(purchase: Purchase): Unit = withContext(dispatchers.default) {
    given<BillingPrefs>().purchases.updateData { it + purchase }
}

@Reader
suspend fun removePurchase(purchaseToken: String): Unit = withContext(dispatchers.default) {
    given<BillingPrefs>().purchases.updateData { purchases ->
        purchases.filter { it.purchaseToken != purchaseToken }
    }
    given<BillingPrefs>().purchases.data.first()
        .filter { it.purchaseToken != purchaseToken }
        .let { given<BillingPrefs>().purchases.updateData { it } }
}

@Reader
suspend fun clearPurchases(): Unit = withContext(dispatchers.default) {
    given<BillingPrefs>().purchases.updateData { emptyList() }
}

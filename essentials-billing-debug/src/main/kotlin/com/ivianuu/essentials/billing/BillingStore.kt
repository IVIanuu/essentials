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
import com.ivianuu.essentials.store.map
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.stringList
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.withContext

@ApplicationScope
@Single
class BillingStore(
    boxFactory: PrefBoxFactory,
    private val dispatchers: AppDispatchers
) {

    private val products = boxFactory.stringList("billing_products")
        .map(
            fromRaw = { productsJson -> productsJson.map { SkuDetails(it) } },
            toRaw = { products -> products.map { it.originalJson } }
        )

    private val purchases = boxFactory.stringList("billing_purchases")
        .map(
            fromRaw = { purchasesJson ->
                purchasesJson.map { purchase ->
                    val params = purchase.split("=:=")
                    Purchase(params[0], params[1])
                }
            },
            toRaw = { purchases ->
                purchases.map { purchase ->
                    purchase.originalJson + "=:=" + purchase.signature
                }
            }
        )

    suspend fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails> = withContext(dispatchers.default) {
        products.get().filter { it.sku in params.skusList && it.type == params.skuType }
    }

    suspend fun getPurchases(@SkuType skuType: String): PurchasesResult = withContext(dispatchers.default) {
        InternalPurchasesResult(BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build(),
            purchases.get().filter { it.signature.endsWith(skuType) })
    }

    suspend fun getPurchaseByToken(purchaseToken: String): Purchase? = withContext(dispatchers.default) {
        purchases.get().firstOrNull { it.purchaseToken == purchaseToken }
    }

    suspend fun addProduct(skuDetails: SkuDetails) = withContext(dispatchers.default) {
        products.get()
            .toMutableList()
            .also { it += skuDetails }
            .let { products.set(it) }
    }

    suspend fun removeProduct(sku: String) = withContext(dispatchers.default) {
        products.get()
            .filter { it.sku != sku }
            .let { products.set(it) }
    }

    suspend fun clearProducts() = withContext(dispatchers.default) {
        products.delete()
    }

    suspend fun addPurchase(purchase: Purchase) = withContext(dispatchers.default) {
        purchases.get()
            .toMutableList()
            .also { it += purchase }
            .let { purchases.set(it) }
    }

    suspend fun removePurchase(purchaseToken: String) = withContext(dispatchers.default) {
        purchases.get()
            .filter { it.purchaseToken != purchaseToken }
            .let { purchases.set(it) }
    }

    suspend fun clearPurchases() = withContext(dispatchers.default) {
        purchases.delete()
    }

}

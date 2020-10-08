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

package com.ivianuu.essentials.billing.debug

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.InternalPurchasesResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ImplBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface BillingStore {
    suspend fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails>

    suspend fun addProduct(skuDetails: SkuDetails)

    suspend fun removeProduct(sku: String)

    suspend fun clearProducts()

    suspend fun getPurchases(@SkuType skuType: String): PurchasesResult

    suspend fun getPurchaseByToken(purchaseToken: String): Purchase?

    suspend fun addPurchase(purchase: Purchase)

    suspend fun removePurchase(purchaseToken: String)

    suspend fun clearPurchases()
}

@ImplBinding(ApplicationComponent::class)
class RealBillingStore(
    private val billingPrefs: BillingPrefs,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : BillingStore {

    override suspend fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails> =
        withContext(dispatchers.default) {
            billingPrefs.products.data.first()
                .filter { it.sku in params.skusList && it.type == params.skuType }
        }

    override suspend fun addProduct(skuDetails: SkuDetails): Unit =
        withContext(dispatchers.default) {
            billingPrefs.products.updateData { it + skuDetails }
        }

    override suspend fun removeProduct(sku: String): Unit = withContext(dispatchers.default) {
        billingPrefs.products.updateData { products ->
            products.filter { it.sku != sku }
        }
    }

    override suspend fun clearProducts(): Unit = withContext(dispatchers.default) {
        billingPrefs.products.updateData { emptyList() }
    }

    override suspend fun getPurchases(@SkuType skuType: String): PurchasesResult =
        withContext(dispatchers.default) {
            InternalPurchasesResult(
                BillingResult.newBuilder()
                    .setResponseCode(BillingClient.BillingResponseCode.OK).build(),
                billingPrefs.purchases.data.first().filter { it.signature.endsWith(skuType) })
        }.also {
            logger.d("got purchase result for $skuType -> ${it.responseCode} ${it.purchasesList}")
        }

    override suspend fun getPurchaseByToken(purchaseToken: String): Purchase? =
        withContext(dispatchers.default) {
            billingPrefs.purchases.data.first()
                .firstOrNull { it.purchaseToken == purchaseToken }
        }

    override suspend fun addPurchase(purchase: Purchase): Unit = withContext(dispatchers.default) {
        billingPrefs.purchases.updateData { it + purchase }
    }

    override suspend fun removePurchase(purchaseToken: String): Unit =
        withContext(dispatchers.default) {
            billingPrefs.purchases.updateData { purchases ->
                purchases.filter { it.purchaseToken != purchaseToken }
            }
            billingPrefs.purchases.data.first()
                .filter { it.purchaseToken != purchaseToken }
                .let { billingPrefs.purchases.updateData { it } }
        }

    override suspend fun clearPurchases(): Unit = withContext(dispatchers.default) {
        billingPrefs.purchases.updateData { emptyList() }
    }

}
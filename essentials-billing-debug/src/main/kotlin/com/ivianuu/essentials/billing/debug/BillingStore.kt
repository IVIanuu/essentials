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

import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.Purchase.PurchasesResult
import com.ivianuu.essentials.coroutines.DefaultDispatcher
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
class BillingStoreImpl(
    private val defaultDispatcher: DefaultDispatcher,
    private val logger: Logger,
    private val productsPref: DebugProductsPref,
    private val purchasesPref: DebugPurchasesPref,
) : BillingStore {

    override suspend fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails> =
        withContext(defaultDispatcher) {
            productsPref.data.first()
                .filter { it.sku in params.skusList && it.type == params.skuType }
        }

    override suspend fun addProduct(skuDetails: SkuDetails): Unit =
        withContext(defaultDispatcher) {
            productsPref.updateData { it + skuDetails }
        }

    override suspend fun removeProduct(sku: String): Unit = withContext(defaultDispatcher) {
        productsPref.updateData { products ->
            products.filter { it.sku != sku }
        }
    }

    override suspend fun clearProducts(): Unit = withContext(defaultDispatcher) {
        productsPref.updateData { emptyList() }
    }

    override suspend fun getPurchases(@SkuType skuType: String): PurchasesResult =
        withContext(defaultDispatcher) {
            InternalPurchasesResult(
                BillingResult.newBuilder()
                    .setResponseCode(BillingClient.BillingResponseCode.OK).build(),
                purchasesPref.data.first().filter { it.signature.endsWith(skuType) }
            )
        }.also {
            logger.d("got purchase result for $skuType -> ${it.responseCode} ${it.purchasesList}")
        }

    override suspend fun getPurchaseByToken(purchaseToken: String): Purchase? =
        withContext(defaultDispatcher) {
            purchasesPref.data.first()
                .firstOrNull { it.purchaseToken == purchaseToken }
        }

    override suspend fun addPurchase(purchase: Purchase): Unit = withContext(defaultDispatcher) {
        purchasesPref.updateData { it + purchase }
    }

    override suspend fun removePurchase(purchaseToken: String): Unit =
        withContext(defaultDispatcher) {
            purchasesPref.updateData { purchases ->
                purchases.filter { it.purchaseToken != purchaseToken }
            }
            purchasesPref.data.first()
                .filter { it.purchaseToken != purchaseToken }
                .let { purchasesPref.updateData { it } }
        }

    override suspend fun clearPurchases(): Unit = withContext(defaultDispatcher) {
        purchasesPref.updateData { emptyList() }
    }
}

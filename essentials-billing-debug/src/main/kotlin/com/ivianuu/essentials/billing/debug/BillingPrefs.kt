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
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.map
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

internal typealias DebugProductsPref = DataStore<List<SkuDetails>>

@Binding(ApplicationComponent::class)
fun debugProductsPref(factory: DiskDataStoreFactory): DebugProductsPref =
    factory.create("billing_products") { emptySet<String>() }
        .map(
            fromRaw = { productsJson ->
                productsJson.map { SkuDetails(it) }
            },
            toRaw = { products -> products.map { it.originalJson }.toSet() }
        )

internal typealias DebugPurchasesPref = DataStore<List<Purchase>>

@Binding(ApplicationComponent::class)
fun debugPurchasesPref(factory: DiskDataStoreFactory): DebugPurchasesPref =
    factory.create("billing_purchases") { emptySet<String>() }
        .map(
            fromRaw = { purchasesJson ->
                purchasesJson.map { purchase ->
                    val params = purchase.split("=:=")
                    com.android.billingclient.api.Purchase(params[0], params[1])
                }
            },
            toRaw = { purchases ->
                purchases.map { purchase ->
                    purchase.originalJson + "=:=" + purchase.signature
                }.toSet()
            }
        )
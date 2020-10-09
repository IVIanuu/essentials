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
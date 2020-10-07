package com.ivianuu.essentials.billing.debug

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.map
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

@Binding(ApplicationComponent::class)
class BillingPrefs(factory: DiskDataStoreFactory) {
    val products = factory.create("billing_products") { emptySet<String>() }
        .map(
            fromRaw = { productsJson ->
                productsJson.map {
                    SkuDetails(it)
                }
            },
            toRaw = { products -> products.map { it.originalJson }.toSet() }
        )

    val purchases = factory.create("billing_purchases") { emptySet<String>() }
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
                }.toSet()
            }
        )
}

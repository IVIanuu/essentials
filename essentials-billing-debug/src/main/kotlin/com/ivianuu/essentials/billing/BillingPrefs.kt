package com.ivianuu.essentials.billing

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.map
import com.ivianuu.essentials.store.prefs.stringSet
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.Single

@ApplicationScope
@Single
internal class BillingPrefs(factory: PrefBoxFactory) {
    val products = factory.stringSet("billing_products")
        .map(
            fromRaw = { productsJson -> productsJson.map {
                SkuDetails(it)
            } },
            toRaw = { products -> products.map { it.originalJson }.toSet() }
        )

    val purchases = factory.stringSet("billing_purchases")
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

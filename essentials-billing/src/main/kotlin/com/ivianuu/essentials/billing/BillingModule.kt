package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

object BillingModule {

    @Given(ApplicationComponent::class)
    fun billingClient(): BillingClient {
        return given(object : PurchasesUpdatedListener {
            override fun onPurchasesUpdated(
                result: BillingResult?,
                purchases: MutableList<Purchase>?
            ) {
                d { "on purchases update ${result?.responseCode} ${result?.debugMessage} $purchases" }
                refreshTrigger.offer(Unit)
            }
        })
    }

}

package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

object EsBillingGivens {

    @Given(ApplicationContext::class)
    fun billingClient(): BillingClient {
        val listener = PurchasesUpdatedListener { result, purchases ->
            refreshTrigger.offer(Unit)
        }
        return given(listener)
    }

}

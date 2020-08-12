package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.ivianuu.injekt.ApplicationStorage
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

object EsBillingModule {

    @Given(ApplicationStorage::class)
    fun billingClient(): BillingClient {
        val listener = PurchasesUpdatedListener { result, purchases ->
            refreshTrigger.offer(Unit)
        }
        return given(listener)
    }

}

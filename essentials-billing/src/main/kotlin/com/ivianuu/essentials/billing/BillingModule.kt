package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

object BillingModule {

    @Given(ApplicationComponent::class)
    fun billingClient(): BillingClient {
        val listener = PurchasesUpdatedListener { result, purchases ->
            d { "on purchases update ${result?.responseCode} ${result?.debugMessage} $purchases" }
            refreshTrigger.offer(Unit)
        }
        return given(listener)
    }

}

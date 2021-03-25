package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped

@Given
fun billingClient(
    @Given appContext: AppContext,
    @Given updateListener: PurchasesUpdatedListener,
): @Scoped<AppGivenScope> BillingClient = BillingClient
    .newBuilder(appContext)
    .enablePendingPurchases()
    .setListener(updateListener)
    .build()

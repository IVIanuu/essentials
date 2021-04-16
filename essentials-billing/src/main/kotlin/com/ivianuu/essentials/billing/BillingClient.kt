package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

@Given
fun billingClient(
    @Given appContext: AppContext,
    @Given updateListener: PurchasesUpdatedListener,
): @Scoped<AppGivenScope> BillingClient = BillingClient
    .newBuilder(appContext)
    .enablePendingPurchases()
    .setListener(updateListener)
    .build()

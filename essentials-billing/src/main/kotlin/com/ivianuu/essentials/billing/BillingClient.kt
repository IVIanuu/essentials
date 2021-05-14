package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Given fun billingClient(
  @Given appContext: AppContext,
  @Given refreshes: MutableSharedFlow<BillingRefresh>
): @Scoped<AppGivenScope> BillingClient = BillingClient
  .newBuilder(appContext)
  .enablePendingPurchases()
  .setListener { _, _ -> refreshes.tryEmit(BillingRefresh) }
  .build()

typealias BillingRefresh = Unit

@Given val billingRefreshes: @Scoped<AppGivenScope> MutableSharedFlow<BillingRefresh>
  get() = EventFlow()

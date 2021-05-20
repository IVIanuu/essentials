package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Provide fun billingClient(
  context: AppContext,
  refreshes: MutableSharedFlow<BillingRefresh>
): @Scoped<AppScope> BillingClient = BillingClient
  .newBuilder(context)
  .enablePendingPurchases()
  .setListener { _, _ -> refreshes.tryEmit(BillingRefresh) }
  .build()

typealias BillingRefresh = Unit

@Provide val billingRefreshes: @Scoped<AppScope> MutableSharedFlow<BillingRefresh>
  get() = EventFlow()

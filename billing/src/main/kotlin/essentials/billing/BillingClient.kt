/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.billing

import com.android.billingclient.api.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide fun billingClient(
  appContext: AppContext,
  refreshes: MutableSharedFlow<BillingRefresh>
): BillingClient = BillingClient
  .newBuilder(appContext)
  .enablePendingPurchases()
  .setListener { _, _ -> refreshes.tryEmit(BillingRefresh) }
  .build()

object BillingRefresh

@Provide val billingRefreshes = EventFlow<BillingRefresh>()

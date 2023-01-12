/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.inject
import kotlinx.coroutines.flow.MutableSharedFlow

context(AppContext) @Provide fun billingClient(
  refreshes: MutableSharedFlow<BillingRefresh>
): @Scoped<AppScope> BillingClient = BillingClient
  .newBuilder(inject())
  .enablePendingPurchases()
  .setListener { _, _ -> refreshes.tryEmit(BillingRefresh) }
  .build()

object BillingRefresh

@Provide val billingRefreshes = EventFlow<BillingRefresh>()

/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.MutableSharedFlow

@Provide @Scoped<AppComponent> fun billingClient(
  context: AppContext,
  refreshes: MutableSharedFlow<BillingRefresh>
): BillingClient = BillingClient
  .newBuilder(context)
  .enablePendingPurchases()
  .setListener { _, _ -> refreshes.tryEmit(BillingRefresh) }
  .build()

typealias BillingRefresh = Unit

@Provide @Scoped<AppComponent> val billingRefreshes: MutableSharedFlow<BillingRefresh>
  get() = EventFlow()

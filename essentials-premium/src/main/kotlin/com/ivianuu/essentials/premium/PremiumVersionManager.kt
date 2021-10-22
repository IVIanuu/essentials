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

package com.ivianuu.essentials.premium

import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.billing.GetSkuDetailsUseCase
import com.ivianuu.essentials.billing.IsPurchased
import com.ivianuu.essentials.billing.PurchaseUseCase
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn

typealias PremiumVersionSku = Sku

typealias OldPremiumVersionSku = Sku

@Provide @Scoped<AppComponent> class PremiumVersionManager(
  private val getSkuDetails: GetSkuDetailsUseCase,
  private val premiumVersionSku: PremiumVersionSku,
  oldPremiumVersionSkus: List<OldPremiumVersionSku> = emptyList(),
  isPurchased: (Sku) -> Flow<IsPurchased>,
  private val purchaseUseCase: PurchaseUseCase,
  scope: ComponentScope<AppComponent>
) {
  val premiumSkuDetails: Flow<SkuDetails>
    get() = flow { emit(getSkuDetails(premiumVersionSku)!!) }

  val isPremiumVersion: Flow<Boolean> = combine(
    isPurchased(premiumVersionSku),
    if (oldPremiumVersionSkus.isNotEmpty()) combine(oldPremiumVersionSkus.map(isPurchased)) {
      it.any { it }
    } else flowOf(false)
  ) { a, b -> a || b }
    .shareIn(scope, SharingStarted.Eagerly, 1)

  suspend fun purchasePremiumVersion() {
    purchaseUseCase(premiumVersionSku, true, true)
  }
}

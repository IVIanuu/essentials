/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample

import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.billing.BillingStore
import com.ivianuu.essentials.billing.SkuDetails
import com.ivianuu.essentials.sample.ui.DummySku
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.ForApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Factory
class PurchaseInitializer(
    billingStore: BillingStore,
    @ForApplication coroutineScope: CoroutineScope
) :
    AppInitializer {
    init {
        coroutineScope.launch {
            billingStore.addProduct(SkuDetails(DummySku))
        }
    }
}
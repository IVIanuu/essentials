/*
 * Copyright 2020 Manuel Wrage
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

import com.ivianuu.essentials.android.prefs.PrefUpdater
import com.ivianuu.essentials.app.ScopeInitializer
import com.ivianuu.essentials.billing.debug.DebugBillingPrefs
import com.ivianuu.essentials.billing.debug.SkuDetails
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.sample.ui.DummySku
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.AppComponent
import kotlinx.coroutines.launch

@Given
fun debugPurchaseAppInitializer(
    @Given globalScope: GlobalScope,
    @Given updateDebugPrefs: PrefUpdater<DebugBillingPrefs>,
): ScopeInitializer<AppComponent> = {
    globalScope.launch {
        updateDebugPrefs {
            copy(products = products + SkuDetails(DummySku))
        }
    }
}

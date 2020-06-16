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

package com.ivianuu.essentials.billing

import androidx.compose.Composable
import androidx.compose.launchInComposition
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.injekt.Transient

@Transient
class PurchasePage(
    private val manager: PurchaseManager,
    private val navigator: Navigator
) {
    @Composable
    operator fun invoke(requestId: String) {
        val activity = compositionActivity
        val route = RouteAmbient.current
        launchInComposition {
            manager.purchaseInternal(requestId, activity)
            navigator.pop(route = route)
        }
    }
}

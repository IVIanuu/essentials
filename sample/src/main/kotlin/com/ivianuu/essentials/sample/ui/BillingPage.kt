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

package com.ivianuu.essentials.sample.ui

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.billing.PurchaseManager
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.coroutines.collectAsState
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class BillingPage(
    private val purchaseManager: PurchaseManager
) {
    @Composable
    operator fun invoke() {
        val isPurchased = remember { purchaseManager.isPurchased(DummySku) }
            .collectAsState(false)

        Scaffold(
            topAppBar = { TopAppBar(title = { Text("Billing") }) },
            body = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalGravity = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Is purchased ? ${isPurchased.value}",
                        style = MaterialTheme.typography.h6
                    )

                    Spacer(Modifier.height(8.dp))

                    if (!isPurchased.value) {
                        Button(
                            onClick = launchOnClick { purchaseManager.purchase(DummySku) }
                        ) { Text("Purchase") }
                    } else {
                        Button(
                            onClick = launchOnClick { purchaseManager.consume(DummySku) }
                        ) { Text("Consume") }
                    }
                }
            }
        )
    }
}

val DummySku = Sku(skuString = "dummy", type = Sku.Type.InApp)

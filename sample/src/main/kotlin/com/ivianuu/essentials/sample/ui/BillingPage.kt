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

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.billing.consumePurchase
import com.ivianuu.essentials.billing.isPurchased
import com.ivianuu.essentials.billing.purchase
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.launch

@Reader
@Composable
fun BillingPage() {
    val isPurchased = remember { isPurchased(DummySku) }
        .collectAsState(false)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Billing") }) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Is purchased ? ${isPurchased.value}",
                style = MaterialTheme.typography.h6
            )

            Spacer(Modifier.height(8.dp))

            val scope = rememberCoroutineScope()

            if (!isPurchased.value) {
                Button(
                    onClick = {
                        scope.launch {
                            purchase(DummySku)
                        }
                    }
                ) { Text("Purchase") }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            consumePurchase(DummySku)
                        }
                    }
                ) { Text("Consume") }
            }
        }
    }
}

val DummySku = Sku(skuString = "dummy", type = Sku.Type.InApp)

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

package com.ivianuu.essentials.billing.debug

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogNavigationOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.NavigationOptionFactoryBinding
import com.ivianuu.essentials.ui.navigation.popTopKeyWithResult
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.essentials.ui.resource.produceResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class DebugPurchaseKey(val params: BillingFlowParams)

@KeyUiBinding<DebugPurchaseKey>
@GivenFun
@Composable
fun DebugPurchaseDialog(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given key: DebugPurchaseKey,
    @Given popTopKeyWithResult: popTopKeyWithResult<SkuDetails>,
    @Given prefs: Flow<DebugBillingPrefs>,
) {
    DialogWrapper {
        ResourceBox(
            resource = produceResource {
                withContext(defaultDispatcher) {
                    prefs.first().products.firstOrNull() {
                        it.type == key.params.skuType &&
                                it.sku == key.params.sku
                    }
                }
            },
            loading = {
                Dialog(
                    content = {
                        CircularProgressIndicator(
                            modifier = Modifier.center()
                        )
                    }
                )
            }
        ) { skuDetails ->
            if (skuDetails == null) {
                popTopKeyWithResult(null)
                return@ResourceBox
            }
            Dialog(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = skuDetails.title)

                        Spacer(Modifier.weight(1f))

                        Text(
                            text = skuDetails.price,
                            style = MaterialTheme.typography.subtitle1.copy(
                                color = GooglePlayGreen
                            )
                        )
                    }
                },
                content = { Text(skuDetails.description) },
                positiveButton = {
                    Button(
                        colors = ButtonConstants.defaultButtonColors(
                            backgroundColor = GooglePlayGreen,
                            contentColor = guessingContentColorFor(GooglePlayGreen),
                        ),
                        onClick = {
                            popTopKeyWithResult(skuDetails)
                        }
                    ) { Text(R.string.purchase) }
                }
            )
        }
    }
}

@NavigationOptionFactoryBinding
@Given
val debugPurchaseDialogNavigationOptionsFactory
    get() = DialogNavigationOptionsFactory<DebugPurchaseKey>()

private val GooglePlayGreen = Color(0xFF00A273)

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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.billing.debug.DebugPurchaseAction.Purchase
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKeyUiOptionsFactory
import com.ivianuu.essentials.ui.dialog.DialogWrapper
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.essentials.ui.resource.Error
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

data class DebugPurchaseKey(val sku: Sku) : Key<SkuDetails>

@Given
val debugPurchaseKeyModule = KeyModule<DebugPurchaseKey>()

@Given
fun debugPurchaseUi(
    @Given stateFlow: StateFlow<DebugPurchaseState>,
    @Given dispatch: Collector<DebugPurchaseAction>
): KeyUi<DebugPurchaseKey> = {
    DialogWrapper {
        val state by stateFlow.collectAsState()
        val skuDetails = state.skuDetails()
        Dialog(
            title = skuDetails?.let {
                {
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
                }
            },
            content = {
                  if (skuDetails == null) {
                      CircularProgressIndicator()
                  } else  {
                      Text(skuDetails.description)
                  }
            },
            positiveButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = GooglePlayGreen,
                        contentColor = guessingContentColorFor(GooglePlayGreen),
                    ),
                    onClick = { dispatch(Purchase) }
                ) { Text(stringResource(R.string.purchase)) }
            }
        )
    }
}

@Given
val debugPurchaseUiOptionsFactory = DialogKeyUiOptionsFactory<DebugPurchaseKey>()

private val GooglePlayGreen = Color(0xFF00A273)

data class DebugPurchaseState(
    val skuDetails: Resource<SkuDetails> = Idle
)

sealed class DebugPurchaseAction {
    object Purchase : DebugPurchaseAction()
}

@Scoped<KeyUiComponent>
@Given
fun debugPurchaseState(
    @Given scope: ScopeCoroutineScope<KeyUiComponent>,
    @Given initial: @Initial DebugPurchaseState = DebugPurchaseState(),
    @Given actions: Flow<DebugPurchaseAction>,
    @Given key: DebugPurchaseKey,
    @Given navigator: Collector<NavigationAction>,
    @Given prefs: Flow<DebugBillingPrefs>
): StateFlow<DebugPurchaseState> = scope.state(initial) {
    reduceResource(block = {
        prefs.first().products.firstOrNull {
            it.type == key.sku.type.value &&
                    it.sku == key.sku.skuString
        }!!
    }) { copy(skuDetails = it) }

    state
        .map { it.skuDetails }
        .filterIsInstance<Error>()
        .onEach { navigator(Pop(key)) }
        .launchIn(this)

    actions
        .filterIsInstance<Purchase>()
        .onEach { navigator(Pop(key, state.first().skuDetails()!!)) }
        .launchIn(this)
}

@Scoped<KeyUiComponent>
@Given
val debugPurchaseActions get() = EventFlow<DebugPurchaseAction>()


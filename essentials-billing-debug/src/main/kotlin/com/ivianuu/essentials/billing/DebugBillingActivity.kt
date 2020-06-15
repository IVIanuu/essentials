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

import android.content.Context
import android.content.Intent
import androidx.compose.Composable
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.graphics.Color
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.billing.debug.R
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.common.RenderAsync
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.material.ContainedButtonStyle
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.navigation.DialogRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.launchAsync
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.android.AndroidEntryPoint
import com.ivianuu.injekt.inject
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class DebugBillingActivity : EsActivity() {

    private val client: DebugBillingClient by inject()
    private val navigator: Navigator by inject()
    private val purchaseDialog: PurchaseDialog by inject()

    private lateinit var requestId: String
    private lateinit var skuDetails: SkuDetails

    @Composable
    override fun content() {
        val requestId = intent.getStringExtra(KEY_REQUEST_ID)
        if (requestId == null) {
            finish()
            return
        }
        this.requestId = requestId

        RenderAsync(
            state = launchAsync(requestId) { client.getSkuDetailsForRequest(requestId) },
            success = {
                if (it == null) {
                    finish()
                } else {
                    this.skuDetails = it
                    if (!navigator.hasRoot) {
                        navigator.setRoot(
                            DialogRoute(
                                onDismiss = {
                                    lifecycleScope.launch {
                                        client.onPurchaseResult(
                                            requestId = requestId,
                                            responseCode = BillingClient.BillingResponseCode.USER_CANCELED,
                                            purchases = null
                                        )

                                        finish()
                                    }
                                }
                            ) {
                                purchaseDialog(
                                    onPurchaseClick = {
                                        lifecycleScope.launch {
                                            client.onPurchaseResult(
                                                requestId = requestId,
                                                responseCode = BillingClient.BillingResponseCode.OK,
                                                purchases = listOf(skuDetails.toPurchaseData())
                                            )

                                            finish()
                                        }
                                    },
                                    skuDetails = skuDetails
                                )
                            }
                        )
                    }
                    navigator()
                }
            }
        )
    }

    private fun SkuDetails.toPurchaseData(): Purchase {
        val json = """{"orderId":"$sku..0","packageName":"$packageName","productId":
      |"$sku","autoRenewing":true,"purchaseTime":"${Date().time}","acknowledged":false,"purchaseToken":
      |"0987654321", "purchaseState":1}""".trimMargin()
        return Purchase(json, "debug-signature-$sku-${skuDetails.type}")
    }

    internal companion object {

        private const val KEY_REQUEST_ID = "request_id"

        fun purchase(
            context: Context,
            requestId: String
        ) {
            context.startActivity(
                Intent(
                    context,
                    DebugBillingActivity::class.java
                ).apply {
                    putExtra(KEY_REQUEST_ID, requestId)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}

@Transient
internal class PurchaseDialog {

    @Composable
    operator fun invoke(
        onPurchaseClick: () -> Unit,
        skuDetails: SkuDetails
    ) {
        Dialog(
            title = {
                Row(verticalGravity = Alignment.CenterVertically) {
                    Text(text = skuDetails.title)

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = skuDetails.price,
                        textStyle = MaterialTheme.typography.subtitle1.copy(
                            color = GooglePlayGreen
                        )
                    )
                }
            },
            content = { Text(skuDetails.description) },
            positiveButton = {
                DialogButton(
                    style = ContainedButtonStyle(
                        backgroundColor = GooglePlayGreen,
                        contentColor = guessingContentColorFor(GooglePlayGreen)
                    ),
                    onClick = onPurchaseClick
                ) { Text(R.string.purchase) }
            }
        )
    }

}

private val GooglePlayGreen = Color(0xFF00A273)

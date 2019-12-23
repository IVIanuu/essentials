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
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Spacer
import androidx.ui.material.ContainedButtonStyle
import androidx.ui.material.MaterialTheme
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.ui.dialog.DialogButton
import com.ivianuu.essentials.ui.dialog.DialogRoute
import com.ivianuu.essentials.ui.dialog.MaterialDialog
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.material.contentColorFor
import com.ivianuu.injekt.get
import com.ivianuu.injekt.inject
import java.util.Date

class DebugBillingActivity : EsActivity() {

    private val client: DebugBillingClient by inject()
    private lateinit var requestId: String
    private lateinit var skuDetails: SkuDetails

    override fun content() {
        get<PurchaseManager>() // just to properly initialize the DebugBillingClient
        val requestId = intent.getStringExtra(KEY_REQUEST_ID)
        if (requestId == null) {
            finish()
            return
        }
        this.requestId = requestId

        val skuDetails = client.getSkuDetailsForRequest(requestId)
        if (skuDetails == null) {
            finish()
            return
        }
        this.skuDetails = skuDetails

        Navigator(startRoute = PurchaseDialogRoute)
    }

    private fun SkuDetails.toPurchaseData(): Purchase {
        val json = """{"orderId":"$sku..0","packageName":"$packageName","productId":
      |"$sku","autoRenewing":true,"purchaseTime":"${Date().time}","acknowledged":false,"purchaseToken":
      |"0987654321", "purchaseState":1}""".trimMargin()
        return Purchase(json, "debug-signature-$sku-${skuDetails.type}")
    }

    private val PurchaseDialogRoute = DialogRoute(
        dismissHandler = {
            client.onPurchaseResult(
                requestId = requestId,
                responseCode = BillingClient.BillingResponseCode.USER_CANCELED,
                purchases = null
            )

            finish()
        }
    ) {
        MaterialDialog(
            title = {
                Row {
                    Text(
                        text = skuDetails.title,
                        modifier = Inflexible
                    )

                    Spacer(Flexible(1f))

                    Text(
                        text = skuDetails.price,
                        modifier = Inflexible,
                        style = MaterialTheme.typography().subtitle1.copy(
                            color = GooglePlayGreen
                        )
                    )
                }
            },
            content = { Text(skuDetails.description) },
            positiveButton = {
                DialogButton(
                    text = "Purchase",
                    style = ContainedButtonStyle(
                        backgroundColor = GooglePlayGreen,
                        contentColor = contentColorFor(GooglePlayGreen)
                    ),
                    onClick = {
                        client.onPurchaseResult(
                            requestId = requestId,
                            responseCode = BillingClient.BillingResponseCode.OK,
                            purchases = listOf(skuDetails.toPurchaseData())
                        )

                        finish()
                    }
                )
            }
        )
    }

    internal companion object {
        private val GooglePlayGreen = Color(0xFF00A273)

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

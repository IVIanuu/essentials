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
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.billing.debug.R
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.material.guessingContentColorFor

@Composable
internal fun DebugPurchaseDialog(
    skuDetails: SkuDetails,
    onPurchaseClick: () -> Unit
) {
    Dialog(
        title = {
            Row(verticalGravity = Alignment.CenterVertically) {
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
                backgroundColor = GooglePlayGreen,
                contentColor = guessingContentColorFor(GooglePlayGreen),
                onClick = onPurchaseClick
            ) { Text(R.string.purchase) }
        }
    )
}

private val GooglePlayGreen = Color(0xFF00A273)

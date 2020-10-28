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

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.android.billingclient.api.SkuDetails
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.material.guessingContentColorFor

@Composable
internal fun DebugPurchaseDialog(
    onPurchaseClick: () -> Unit,
    skuDetails: SkuDetails
) {
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
                onClick = onPurchaseClick
            ) { Text(R.string.purchase) }
        }
    )
}

private val GooglePlayGreen = Color(0xFF00A273)

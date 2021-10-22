/*
 * Copyright 2021 Manuel Wrage
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

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams

data class Sku(val skuString: String, val type: Type) {
  enum class Type(val value: String) {
    IN_APP(BillingClient.SkuType.INAPP),
    SUBS(BillingClient.SkuType.SUBS)
  }
}

fun Sku.toSkuDetailsParams() = SkuDetailsParams.newBuilder()
  .setType(type.value)
  .setSkusList(listOf(skuString))
  .build()

fun String.toSkuType() = when (this) {
  BillingClient.SkuType.INAPP -> Sku.Type.IN_APP
  BillingClient.SkuType.SUBS -> Sku.Type.SUBS
  else -> throw IllegalArgumentException("Unexpected value $this")
}

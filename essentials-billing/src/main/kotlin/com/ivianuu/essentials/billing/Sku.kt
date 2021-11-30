/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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

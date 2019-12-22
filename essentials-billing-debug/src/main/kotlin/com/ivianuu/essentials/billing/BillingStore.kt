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
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.InternalPurchasesResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import org.json.JSONArray
import org.json.JSONObject

@ApplicationScope
@Single
class BillingStore(context: Context) {

    private val prefs = context.getSharedPreferences("dbx", Context.MODE_PRIVATE)

    fun getSkuDetails(params: SkuDetailsParams): List<SkuDetails> {
        return prefs.getString(KEY_SKU_DETAILS, "[]")!!.toSkuDetailsList()
            .filter { it.sku in params.skusList && it.type == params.skuType }
    }

    fun getPurchases(@SkuType skuType: String): PurchasesResult {
        return InternalPurchasesResult(BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build(),
            prefs.getString(KEY_PURCHASES, "[]")!!.toPurchaseList()
                .filter { it.signature.endsWith(skuType) })
    }

    fun getPurchaseByToken(purchaseToken: String): Purchase? {
        return prefs.getString(KEY_PURCHASES, "[]")!!.toPurchaseList()
            .firstOrNull { it.purchaseToken == purchaseToken }
    }

    fun addProduct(skuDetails: SkuDetails): BillingStore {
        val allDetails = JSONArray(prefs.getString(KEY_SKU_DETAILS, "[]"))
        allDetails.put(skuDetails.toJSONObject())
        prefs.edit().putString(KEY_SKU_DETAILS, allDetails.toString()).apply()
        return this
    }

    fun removeProduct(sku: String): BillingStore {
        val allDetails = prefs.getString(KEY_SKU_DETAILS, "[]")!!.toSkuDetailsList()
        val filtered = allDetails.filter { it.sku != sku }
        val json = JSONArray()
        filtered.forEach { json.put(it.toJSONObject()) }
        prefs.edit().putString(KEY_SKU_DETAILS, json.toString()).apply()
        return this
    }

    fun clearProducts(): BillingStore {
        prefs.edit().remove(KEY_SKU_DETAILS).apply()
        return this
    }

    fun addPurchase(purchase: Purchase): BillingStore {
        val allPurchases = JSONArray(prefs.getString(KEY_PURCHASES, "[]"))
        allPurchases.put(purchase.toJSONObject())
        prefs.edit().putString(KEY_PURCHASES, allPurchases.toString()).apply()
        return this
    }

    fun removePurchase(purchaseToken: String): BillingStore {
        val allPurchases = prefs.getString(KEY_PURCHASES, "[]")!!.toPurchaseList()
        val filtered = allPurchases.filter { it.purchaseToken != purchaseToken }
        val json = JSONArray()
        filtered.forEach { json.put(it.toJSONObject()) }
        prefs.edit().putString(KEY_PURCHASES, json.toString()).apply()
        return this
    }

    fun clearPurchases(): BillingStore {
        prefs.edit().remove(KEY_PURCHASES).apply()
        return this
    }

    private fun Purchase.toJSONObject(): JSONObject =
        JSONObject().put("purchase", JSONObject(originalJson)).put("signature", signature)

    private fun JSONObject.toPurchase(): Purchase =
        Purchase(this.getJSONObject("purchase").toString(), this.getString("signature"))

    private fun SkuDetails.toJSONObject(): JSONObject = JSONObject(originalJson)

    private fun JSONObject.toSkuDetails(): SkuDetails = SkuDetails(toString())

    private fun String.toPurchaseList(): List<Purchase> {
        val list = mutableListOf<Purchase>()
        val array = JSONArray(this)
        (0 until array.length()).mapTo(list) { array.getJSONObject(it).toPurchase() }
        return list
    }

    private fun String.toSkuDetailsList(): List<SkuDetails> {
        val list = mutableListOf<SkuDetails>()
        val array = JSONArray(this)
        (0 until array.length()).mapTo(list) { array.getJSONObject(it).toSkuDetails() }
        return list
    }

    companion object {
        internal const val KEY_PURCHASES = "dbc_purchases"
        internal const val KEY_SKU_DETAILS = "dbc_sku_details"
    }

}
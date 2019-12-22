package com.android.billingclient.api

internal class InternalPurchasesResult(
    responseCode: BillingResult,
    purchasesList: List<Purchase>?
) : Purchase.PurchasesResult(responseCode, purchasesList)
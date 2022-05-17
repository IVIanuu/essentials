/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Icon
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.premium.AppFeature
import com.ivianuu.essentials.premium.GoPremiumKey
import com.ivianuu.essentials.premium.PremiumVersionSku
import com.ivianuu.essentials.sample.R
import com.ivianuu.injekt.Provide

@Provide val premiumHomeItem = HomeItem("Premium") {
  GoPremiumKey(showTryBasicOption = true)
}

@Provide val premiumVersionSku: PremiumVersionSku = PremiumVersionSku(
  "id",
  Sku.Type.IN_APP
)

@Provide val premiumFeature = listOf(
  AppFeature(
    title = "Sample feature 1",
    icon = { Icon(R.drawable.es_ic_accessibility) },
    inPremium = false,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 2",
    icon = { Icon(R.drawable.es_ic_notifications) },
    inPremium = true,
    inBasic = false
  ),
  AppFeature(
    title = "Sample feature 3",
    icon = { Icon(R.drawable.es_ic_photo_album) },
    inPremium = true,
    inBasic = false
  ),
  AppFeature(
    title = "Sample feature 4",
    icon = { Icon(R.drawable.es_ic_adb) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 5",
    icon = { Icon(R.drawable.es_ic_adb) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 6",
    icon = { Icon(R.drawable.es_ic_adb) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 7",
    icon = { Icon(R.drawable.es_ic_adb) },
    inPremium = true,
    inBasic = true
  )
)

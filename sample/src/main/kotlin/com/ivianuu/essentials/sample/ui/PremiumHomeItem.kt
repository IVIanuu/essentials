/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Icon
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.premium.AppFeature
import com.ivianuu.essentials.premium.GoPremiumScreen
import com.ivianuu.essentials.premium.PremiumVersionSku
import com.ivianuu.essentials.sample.R
import com.ivianuu.injekt.Provide

@Provide val premiumHomeItem = HomeItem("Premium") {
  GoPremiumScreen(showTryBasicOption = true)
}

@Provide val premiumVersionSku = PremiumVersionSku(
  "id",
  Sku.Type.IN_APP
)

@Provide val premiumFeature = listOf(
  AppFeature(
    title = "Sample feature 1",
    icon = { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_accessibility), null) },
    inPremium = false,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 2",
    icon = { Icon(painterResource(com.ivianuu.essentials.android.R.drawable.ic_notifications), null) },
    inPremium = true,
    inBasic = false
  ),
  AppFeature(
    title = "Sample feature 3",
    icon = { Icon(painterResource(com.ivianuu.essentials.gestures.R.drawable.ic_photo_album), null) },
    inPremium = true,
    inBasic = false
  ),
  AppFeature(
    title = "Sample feature 4",
    icon = { Icon(painterResource(com.ivianuu.essentials.gestures.R.drawable.ic_adb), null) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 5",
    icon = { Icon(painterResource(com.ivianuu.essentials.gestures.R.drawable.ic_adb), null) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 6",
    icon = { Icon(painterResource(com.ivianuu.essentials.gestures.R.drawable.ic_adb), null) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 7",
    icon = { Icon(painterResource(com.ivianuu.essentials.gestures.R.drawable.ic_adb), null) },
    inPremium = true,
    inBasic = true
  )
)

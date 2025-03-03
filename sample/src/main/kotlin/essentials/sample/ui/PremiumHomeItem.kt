/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.premium.*
import injekt.*

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
    icon = { Icon(Icons.Default.Accessibility, null) },
    inPremium = false,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 2",
    icon = { Icon(Icons.Default.Notifications, null) },
    inPremium = true,
    inBasic = false
  ),
  AppFeature(
    title = "Sample feature 3",
    icon = { Icon(Icons.Default.PhotoAlbum, null) },
    inPremium = true,
    inBasic = false
  ),
  AppFeature(
    title = "Sample feature 4",
    icon = { Icon(Icons.Default.Adb, null) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 5",
    icon = { Icon(Icons.Default.Adb, null) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 6",
    icon = { Icon(Icons.Default.Adb, null) },
    inPremium = true,
    inBasic = true
  ),
  AppFeature(
    title = "Sample feature 7",
    icon = { Icon(Icons.Default.Adb, null) },
    inPremium = true,
    inBasic = true
  )
)

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

package com.ivianuu.essentials.sample.ui

import androidx.compose.material.Icon
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.premium.AppFeature
import com.ivianuu.essentials.premium.GoPremiumKey
import com.ivianuu.essentials.premium.PremiumVersionSku
import com.ivianuu.essentials.sample.R
import com.ivianuu.injekt.Provide

@Provide val premiumHomeItem = HomeItem("Premium") { GoPremiumKey(true) }

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
  )
)

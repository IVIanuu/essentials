/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.donation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import arrow.fx.coroutines.parMap
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

class DonationScreen : DialogScreen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      billingService: BillingService,
      commonStrings: CommonStrings,
      donations: Donations,
      navigator: Navigator,
      screen: DonationScreen,
      toaster: Toaster
    ) = Ui<DonationScreen, Unit> {
      val skus = collectResource {
        donations
          .value
          .parMap { donation ->
            val details = billingService.getSkuDetails(donation.sku)!!
            UiDonation(
              donation,
              details.title
                .replaceAfterLast("(", "")
                .removeSuffix("("),
              details.price
            )
          }
          .let { emit(it) }
      }
      DialogScaffold {
        Dialog(
          applyContentPadding = false,
          title = { Text("Support development \uD83D\uDC9B") },
          content = {
            ResourceVerticalListFor(
              resource = skus,
              loading = {
                CircularProgressIndicator(
                  modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .center()
                )
              }
            ) { donation ->
              ListItem(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = action {
                  if (billingService.purchase(donation.donation.sku, true, true)) {
                    billingService.consumePurchase(donation.donation.sku)
                    toaster("Thanks for your support! \uD83D\uDC9B")
                  }
                },
                title = { Text(donation.title) },
                leading = { Icon(painterResource(donation.donation.iconRes), null) },
                trailing = {
                  Text(
                    text = donation.price,
                    style = MaterialTheme.typography.body2,
                    color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                  )
                }
              )
            }
          },
          buttons = {
            TextButton(onClick = action { navigator.pop(screen) }) {
              Text(commonStrings.cancel)
            }
          }
        )
      }
    }

    private data class UiDonation(val donation: Donation, val title: String, val price: String)
  }
}

data class Donation(val sku: Sku, val iconRes: Int)

@JvmInline value class Donations(val value: List<Donation>) {
  @Provide companion object {
    @Provide val default get() = Donations(
        listOf(
          Donation(Sku("donation_crossaint", Sku.Type.IN_APP), R.drawable.ic_bakery_dining),
          Donation(Sku("donation_coffee_2", Sku.Type.IN_APP), R.drawable.ic_free_breakfast),
          Donation(Sku("donation_burger_menu", Sku.Type.IN_APP), R.drawable.ic_lunch_dining),
          Donation(Sku("donation_movie", Sku.Type.IN_APP), R.drawable.ic_popcorn)
        )
      )
  }
}

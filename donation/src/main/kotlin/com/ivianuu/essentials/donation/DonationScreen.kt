/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.donation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import arrow.fx.coroutines.parMap
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

class DonationScreen : OverlayScreen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      billingService: BillingService,
      donations: Donations,
      navigator: Navigator,
      screen: DonationScreen,
      toaster: Toaster
    ) = Ui<DonationScreen> {
      val skus = resourceState {
        value = donations
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
          .success()
      }

      AlertDialog(
        onDismissRequest = action { navigator.pop(screen, null) },
        title = { Text("Support development \uD83D\uDC9B") },
        text = {
          ResourceBox(
            resource = skus,
            loading = {
              CircularProgressIndicator(
                modifier = Modifier
                  .height(100.dp)
                  .fillMaxWidth()
                  .center()
              )
            }
          ) { donations ->
            VerticalList {
              items(donations) { donation ->
                ListItem(
                  modifier = Modifier.padding(horizontal = 8.dp),
                  onClick = action {
                    if (billingService.purchase(donation.donation.sku, true, true)) {
                      billingService.consumePurchase(donation.donation.sku)
                      toaster("Thanks for your support! \uD83D\uDC9B")
                    }
                  },
                  title = { Text(donation.title) },
                  leading = donation.donation.icon,
                  trailing = {
                    Text(
                      text = donation.price,
                      style = MaterialTheme.typography.body2,
                      color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                    )
                  }
                )
              }
            }
          }
        },
        confirmButton = {
          TextButton(onClick = action { navigator.pop(screen) }) {
            Text("Cancel")
          }
        }
      )
    }

    private data class UiDonation(val donation: Donation, val title: String, val price: String)
  }
}

data class Donation(val sku: Sku, val icon: @Composable () -> Unit)

@JvmInline value class Donations(val value: List<Donation>) {
  @Provide companion object {
    @Provide val default get() = Donations(
        listOf(
          Donation(Sku("donation_crossaint", Sku.Type.IN_APP)) {
            Icon(Icons.Default.BakeryDining, null)
          },
          Donation(Sku("donation_coffee_2", Sku.Type.IN_APP)) {
            Icon(Icons.Default.BreakfastDining, null)
          },
          Donation(Sku("donation_burger_menu", Sku.Type.IN_APP)) {
            Icon(Icons.Default.BreakfastDining, null)
          },
          Donation(Sku("donation_movie", Sku.Type.IN_APP)) {
            Icon(Icons.Default.Theaters, null)
          }
        )
      )
  }
}

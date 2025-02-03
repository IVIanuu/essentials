/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.donation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import arrow.fx.coroutines.parMap
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.EsListItem
import com.ivianuu.essentials.ui.overlay.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

class DonationScreen : DialogScreen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      billingManager: BillingManager,
      donations: Donations,
      navigator: Navigator,
      screen: DonationScreen,
      toaster: Toaster
    ) = Ui<DonationScreen> {
      val skus by produceScopedState(Resource.Idle()) {
        value = catchResource {
          donations
            .value
            .parMap { donation ->
              val details = billingManager.getSkuDetails(donation.sku)!!
              UiDonation(
                donation,
                details.title
                  .replaceAfterLast("(", "")
                  .removeSuffix("("),
                details.price
              )
            }
        }
      }

      Dialog(
        title = { Text("Support development \uD83D\uDC9B") },
        applyContentPadding = false,
        content = {
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
            EsLazyColumn {
              items(donations) { donation ->
                EsListItem(
                  modifier = Modifier.padding(horizontal = 8.dp),
                  onClick = scopedAction {
                    if (billingManager.purchase(donation.donation.sku, true, true)) {
                      billingManager.consumePurchase(donation.donation.sku)
                      toaster.toast("Thanks for your support! \uD83D\uDC9B")
                    }
                  },
                  headlineContent = { Text(donation.title) },
                  leadingContent = donation.donation.icon,
                  trailingContent = { Text(text = donation.price) }
                )
              }
            }
          }
        },
        buttons = {
          TextButton(onClick = scopedAction { navigator.pop(screen) }) {
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

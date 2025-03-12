/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.donation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastForEach
import arrow.fx.coroutines.parMap
import essentials.billing.*
import essentials.compose.*
import essentials.resource.*
import essentials.ui.material.EsListItem
import essentials.ui.material.EsModalBottomSheet
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*

class DonationScreen(
  val donations: List<Donation> = Donation.DefaultDonations
) : OverlayScreen<Unit>

@Provide @Composable fun DonationUi(
  billingManager: BillingManager,
  navigator: Navigator,
  screen: DonationScreen,
  toaster: Toaster
): Ui<DonationScreen> {
  val skus by produceScopedState(Resource.Idle()) {
    resourceFlow {
      emit(
        screen.donations.parMap { donation ->
          val details = billingManager.getSkuDetails(donation.sku)!!
          UiDonation(
            donation,
            details.title
              .replaceAfterLast("(", "")
              .removeSuffix("("),
            details.price
          )
        }
      )
    }
      .collect { value = it }
  }

  EsModalBottomSheet(
    onDismissRequest = action { navigator.pop(screen, null) }
  ) {
    skus.getOrNull()?.fastForEach { donation ->
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

private data class UiDonation(val donation: Donation, val title: String, val price: String)

data class Donation(val sku: Sku, val icon: @Composable () -> Unit) {
  companion object {
    val DefaultDonations get() = listOf(
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
  }
}

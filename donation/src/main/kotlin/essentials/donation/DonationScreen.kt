/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.donation

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.Resource
import essentials.billing.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*

class DonationScreen(
  val donations: List<Donation> = Donation.DefaultDonations
) : OverlayScreen<Unit>

@Provide @Composable fun DonationUi(
  billing: Billing,
  showToast: showToast,
  context: ScreenContext<DonationScreen> = inject
): Ui<DonationScreen> {
  val skus by produceScopedState(Resource.Idle()) {
    resourceFlow {
      emit(
        context.screen.donations.parMap { donation ->
          val details = billing.getSkuDetails(donation.sku)!!
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

  BottomSheet {
    Subheader { Text("Donate") }

    EsLazyColumn {
      section {
        sectionItems(skus.getOrElse { emptyList() }) { donation, _ ->
          SectionListItem(
            onClick = scopedAction {
              if (billing.purchase(donation.donation.sku, true, true)) {
                billing.consumePurchase(donation.donation.sku)
                showToast("Thanks for your support! \uD83D\uDC9B")
              }
            },
            title = { Text(donation.title) },
            leading = donation.donation.icon,
            trailing = { Text(text = donation.price) }
          )
        }
      }
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

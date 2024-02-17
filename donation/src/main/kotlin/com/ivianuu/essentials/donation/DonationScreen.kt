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

class DonationScreen : DialogScreen<Unit>

data class Donation(val sku: Sku, val iconRes: Int)

@JvmInline value class Donations(val value: List<Donation>) {
  @Provide companion object {
    @Provide val default
      get() = Donations(
        listOf(
          Donation(Sku("donation_crossaint", Sku.Type.IN_APP), R.drawable.ic_bakery_dining),
          Donation(Sku("donation_coffee_2", Sku.Type.IN_APP), R.drawable.ic_free_breakfast),
          Donation(Sku("donation_burger_menu", Sku.Type.IN_APP), R.drawable.ic_lunch_dining),
          Donation(Sku("donation_movie", Sku.Type.IN_APP), R.drawable.ic_popcorn)
        )
      )
  }
}

@Provide fun donationUi(commonStrings: CommonStrings) = Ui<DonationScreen, DonationState> { state ->
  DialogScaffold {
    Dialog(
      applyContentPadding = false,
      title = { Text(stringResource(R.string.donation_title)) },
      content = {
        ResourceVerticalListFor(
          resource = state.skus,
          loading = {
            CircularProgressIndicator(
              modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .center()
            )
          }
        ) { donation ->
          Donation(
            donation = donation,
            onClick = { state.purchase(donation) }
          )
        }
      },
      buttons = {
        TextButton(onClick = state.close) {
          Text(commonStrings.cancel)
        }
      }
    )
  }
}

@Composable private fun Donation(donation: UiDonation, onClick: () -> Unit) {
  ListItem(
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp),
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

data class DonationState(
  val skus: Resource<List<UiDonation>>,
  val close: () -> Unit,
  val purchase: (UiDonation) -> Unit
)

data class UiDonation(
  val donation: Donation,
  val title: String,
  val price: String
)

@Provide fun donationPresenter(
  billingService: BillingService,
  donations: Donations,
  navigator: Navigator,
  screen: DonationScreen,
  toaster: Toaster
) = Presenter {
  DonationState(
    skus = produceResourceState {
      emit(
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
      )
    }.value,
    close = action { navigator.pop(screen) },
    purchase = action { donation ->
      if (billingService.purchase(donation.donation.sku, true, true)) {
        billingService.consumePurchase(donation.donation.sku)
        toaster(R.string.donation_thanks)
      }
    }
  )
}

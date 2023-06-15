/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.donation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.billing.BillingService
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.produceResource
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.dialog.DialogScreen
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

object DonationScreen : DialogScreen<Unit>

data class Donation(val sku: Sku, val iconRes: Int)

@JvmInline value class Donations(val value: List<Donation>) {
  companion object {
    @Provide val default
      get() = Donations(
        listOf(
          Donation(Sku("donation_crossaint", Sku.Type.IN_APP), R.drawable.es_ic_bakery_dining),
          Donation(Sku("donation_coffee_2", Sku.Type.IN_APP), R.drawable.es_ic_free_breakfast),
          Donation(Sku("donation_burger_menu", Sku.Type.IN_APP), R.drawable.es_ic_lunch_dining),
          Donation(Sku("donation_movie", Sku.Type.IN_APP), R.drawable.es_ic_popcorn)
        )
      )
  }
}

@Provide fun donationUi(commonStrings: CommonStrings) = Ui<DonationScreen, DonationModel> { model ->
  DialogScaffold {
    Dialog(
      applyContentPadding = false,
      title = { Text(R.string.es_donation_title) },
      content = {
        ResourceVerticalListFor(
          modifier = Modifier.animateContentSize(),
          resource = model.skus,
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
            onClick = { model.purchase(donation) }
          )
        }
      },
      buttons = {
        TextButton(onClick = model.close) {
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
    leading = { Icon(donation.donation.iconRes) },
    trailing = {
      Text(
        text = donation.price,
        style = MaterialTheme.typography.body2,
        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
      )
    }
  )
}

data class DonationModel(
  val skus: Resource<List<UiDonation>>,
  val close: () -> Unit,
  val purchase: (UiDonation) -> Unit
)

data class UiDonation(
  val donation: Donation,
  val title: String,
  val price: String
)

@Provide fun donationModel(
  billingService: BillingService,
  donations: Donations,
  navigator: Navigator,
  screen: DonationScreen,
  toaster: Toaster
) = Model {
  DonationModel(
    skus = produceResource {
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
    },
    close = action { navigator.pop(screen) },
    purchase = action { donation ->
      if (billingService.purchase(donation.donation.sku, true, true)) {
        billingService.consumePurchase(donation.donation.sku)
        toaster(R.string.es_donation_thanks)
      }
    }
  )
}

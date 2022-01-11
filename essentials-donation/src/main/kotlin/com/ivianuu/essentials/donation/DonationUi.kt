/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.donation

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

object DonationKey : PopupKey<Unit>

data class Donation(val sku: Sku, val iconRes: Int)

@Provide fun donationUi(
  commonStrings: CommonStrings
) = ModelKeyUi<DonationKey, DonationModel> {
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
  consumePurchase: ConsumePurchaseUseCase,
  donations: List<Donation>,
  getSkuDetails: GetSkuDetailsUseCase,
  purchase: PurchaseUseCase,
  T: ToastContext,
  ctx: KeyUiContext<DonationKey>
) = DonationModel(
  skus = produceResource {
    donations
      .parMap { donation ->
        val details = getSkuDetails(donation.sku)!!
        UiDonation(
          donation,
          details.title
            .replaceAfterLast("(", "")
            .removeSuffix("("),
          details.price
        )
      }
  },
  close = action { ctx.navigator.pop(ctx.key) },
  purchase = action { donation ->
    purchase(donation.donation.sku, true, true)
    consumePurchase(donation.donation.sku)
    showToast(R.string.es_donation_thanks)
  }
)

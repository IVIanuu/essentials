/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.essentials.billing.ConsumePurchaseUseCase
import com.ivianuu.essentials.billing.GetSkuDetailsUseCase
import com.ivianuu.essentials.billing.PurchaseUseCase
import com.ivianuu.essentials.billing.Sku
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.produceResource
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide

object DonationKey : DialogKey<Unit>

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

@Provide @Composable fun donationModel(
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

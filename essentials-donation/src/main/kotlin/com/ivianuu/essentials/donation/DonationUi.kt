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
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.analytics.log
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
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.resource.ResourceVerticalListFor
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide

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
          Donation(
            donation = donation,
            onClick = { purchase(donation) }
          )
        }
      },
      buttons = {
        TextButton(onClick = close) {
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
  analytics: Analytics,
  consumePurchase: ConsumePurchaseUseCase,
  donations: List<Donation>,
  getSkuDetails: GetSkuDetailsUseCase,
  purchase: PurchaseUseCase,
  T: ToastContext,
  ctx: KeyUiContext<DonationKey>
) = Model {
  DonationModel(
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
      val purchaseResult = purchase(donation.donation.sku, true, true)
      analytics.log("donation_clicked") {
        put("donation", donation.donation.sku.skuString)
        put("donated", purchaseResult.toString())
      }
      consumePurchase(donation.donation.sku)
      showToast(R.string.es_donation_thanks)
    }
  )
}

/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.donation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.billing.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

object DonationKey : DialogKey<Nothing>

data class Donation(val sku: Sku, val iconRes: Int)

@Provide val donationUi: ModelKeyUi<DonationKey, DonationModel> = {
  DialogScaffold {
    Dialog(
      applyContentPadding = false,
      title = { Text(stringResource(R.string.es_donation_title)) },
      content = {
        ResourceLazyColumnFor(
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
          Text(stringResource(R.string.es_cancel))
        }
      }
    )
  }
}

@Composable private fun Donation(donation: UiDonation, onClick: () -> Unit) {
  ListItem(
    modifier = Modifier
      .padding(horizontal = 8.dp),
    title = { Text(donation.title) },
    leading = { Icon(painterResource(donation.donation.iconRes), null) },
    trailing = {
      Text(
        text = donation.price,
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
      )
    },
    onClick = onClick
  )
}

@Optics data class DonationModel(
  val skus: Resource<List<UiDonation>> = Idle,
  val close: () -> Unit = {},
  val purchase: (UiDonation) -> Unit = {}
)

data class UiDonation(
  val donation: Donation,
  val title: String,
  val price: String
)

@Provide fun donationModel(
  consumePurchase: ConsumePurchaseUseCase,
  donations: Set<Donation>,
  getSkuDetails: GetSkuDetailsUseCase,
  key: DonationKey,
  navigator: Navigator,
  purchase: PurchaseUseCase,
  scope: InjektCoroutineScope<KeyUiScope>,
  rp: ResourceProvider,
  toaster: Toaster
): @Scoped<KeyUiScope> StateFlow<DonationModel> = scope.state(DonationModel()) {
  resourceFlow {
    emit(
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
    )
  }.update { copy(skus = it) }

  action(DonationModel.close()) { navigator.pop(key) }

  action(DonationModel.purchase()) { donation ->
    purchase(donation.donation.sku, true, true)
    consumePurchase(donation.donation.sku)
    showToast(R.string.es_donation_thanks)
  }
}

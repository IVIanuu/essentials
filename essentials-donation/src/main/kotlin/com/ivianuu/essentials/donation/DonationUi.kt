package com.ivianuu.essentials.donation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.android.billingclient.api.*
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
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

data class DonationKey(val donations: List<Donation>) : Key<Nothing>

data class Donation(val sku: Sku, val iconRes: Int)

@Given
val donationUi: ModelKeyUi<DonationKey, DonationModel> = {
    DialogScaffold {
        Dialog(
            applyContentPadding = false,
            title = { Text(stringResource(R.string.es_donation_title)) },
            negativeButton = {
                TextButton(onClick = model.close) {
                    Text(stringResource(R.string.es_cancel))
                }
            }
        ) {
            ResourceLazyColumnFor(
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
        }
    }
}

@Given
val donationUiOptionsFactory = DialogKeyUiOptionsFactory<DonationKey>()

@Composable
private fun Donation(donation: UiDonation, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        title = { Text(donation.details.title) },
        leading = { Icon(painterResource(donation.donation.iconRes), null) },
        trailing = {
            Text(
                text = donation.details.price,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
        },
        onClick = onClick
    )
}

@Optics
data class DonationModel(
    val skus: Resource<List<UiDonation>> = Idle,
    val close: () -> Unit = {},
    val purchase: (UiDonation) -> Unit = {}
)

data class UiDonation(
    val donation: Donation,
    val details: SkuDetails
)

@Given
fun donationModel(
    @Given consumePurchase: ConsumePurchaseUseCase,
    @Given getSkuDetails: GetSkuDetailsUseCase,
    @Given key: DonationKey,
    @Given navigator: Navigator,
    @Given purchase: PurchaseUseCase,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given stringResource: StringResourceProvider,
    @Given toaster: Toaster
): @Scoped<KeyUiGivenScope> StateFlow<DonationModel> = scope.state(DonationModel()) {
    resourceFlow {
        emit(
            key.donations
                .parMap {
                    UiDonation(
                        it,
                        getSkuDetails(it.sku)!!
                    )
                }
        )
    }
        .update { copy(skus = it) }
    action(DonationModel.close()) { navigator.pop(key) }
    action(DonationModel.purchase()) { donation ->
        purchase(donation.donation.sku, true, true)
        consumePurchase(donation.donation.sku)
        toaster(stringResource(R.string.es_donation_thanks, emptyList()))
    }
}

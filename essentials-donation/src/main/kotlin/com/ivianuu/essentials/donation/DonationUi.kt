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

data class DonationKey(val skus: List<Sku>) : Key<Nothing>

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
                            .height(200.dp)
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
private fun Donation(
    donation: SkuDetails,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        title = { Text(donation.title) },
        subtitle = { Text(donation.description) },
        leading = { Icon(painterResource(R.drawable.es_cake), null) },
        trailing = { Text(donation.price) },
        onClick = onClick
    )
}

@Optics
data class DonationModel(
    val skus: Resource<List<SkuDetails>> = Idle,
    val close: () -> Unit = {},
    val purchase: (SkuDetails) -> Unit = {}
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
    resourceFlow { emit(key.skus.parMap { getSkuDetails(it)!! }) }
        .update { copy(skus = it) }
    action(DonationModel.close()) { navigator.pop(key) }
    action(DonationModel.purchase()) { donation ->
        val sku = key.skus.single { it.skuString == donation.sku }
        purchase(sku, true, true)
        consumePurchase(sku)
        toaster(stringResource(R.string.es_donation_thanks, emptyList()))
    }
}

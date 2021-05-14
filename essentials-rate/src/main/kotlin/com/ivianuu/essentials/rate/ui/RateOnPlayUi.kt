package com.ivianuu.essentials.rate.ui

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object RateOnPlayKey : DialogKey<Nothing>

@Given
val rateOnPlayUi: ModelKeyUi<RateOnPlayKey, RateOnPlayModel> = {
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(stringResource(R.string.es_rate_on_play_title)) },
      content = { Text(stringResource(R.string.es_rate_on_play_content)) },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) {
            Text(stringResource(R.string.es_never))
          }
        }

        TextButton(onClick = model.showLater) {
          Text(stringResource(R.string.es_later))
        }

        TextButton(onClick = model.rate) {
          Text(stringResource(R.string.es_rate))
        }
      }
    )
  }
}

@Optics data class RateOnPlayModel(
  val displayShowNever: Boolean = false,
  val rate: () -> Unit = {},
  val showLater: () -> Unit = {},
  val showNever: () -> Unit = {},
)

@Given
fun rateOnPlayModel(
  @Given displayShowNever: DisplayShowNeverUseCase,
  @Given key: RateOnPlayKey,
  @Given navigator: Navigator,
  @Given rateOnPlay: RateOnPlayUseCase,
  @Given scope: GivenCoroutineScope<KeyUiGivenScope>,
  @Given showLater: ShowLaterUseCase,
  @Given showNever: ShowNeverUseCase
): @Scoped<KeyUiGivenScope> StateFlow<RateOnPlayModel> = scope.state(RateOnPlayModel()) {
  launch {
    val showDoNotShowAgain = displayShowNever()
    update { copy(displayShowNever = showDoNotShowAgain) }
  }
  action(RateOnPlayModel.showLater()) { showLater() }
  action(RateOnPlayModel.showNever()) { showNever() }
  action(RateOnPlayModel.rate()) {
    rateOnPlay()
    navigator.pop(key)
  }
}

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

object FeedbackKey : DialogKey<Nothing>

@Provide val feedbackUi: ModelKeyUi<FeedbackKey, FeedbackModel> = {
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(stringResource(R.string.es_feedback_title)) },
      content = { Text(stringResource(R.string.es_feedback_content)) },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) {
            Text(stringResource(R.string.es_never))
          }
        }
        TextButton(onClick = model.showLater) {
          Text(stringResource(R.string.es_later))
        }

        TextButton(onClick = model.openReddit) {
          Text(stringResource(R.string.es_open_reddit))
        }
        TextButton(onClick = model.sendMail) {
          Text(stringResource(R.string.es_send_mail))
        }
      }
    )
  }
}

@Optics data class FeedbackModel(
  val displayShowNever: Boolean = false,
  val showNever: () -> Unit = {},
  val showLater: () -> Unit = {},
  val openReddit: () -> Unit = {},
  val sendMail: () -> Unit = {},
)

@Provide fun feedbackModel(
  displayShowNever: DisplayShowNeverUseCase,
  key: FeedbackKey,
  navigator: Navigator,
  scope: InjectCoroutineScope<KeyUiScope>,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase
): @Scoped<KeyUiScope> StateFlow<FeedbackModel> = scope.state(FeedbackModel()) {
  launch {
    val showDoNotShowAgain = displayShowNever()
    update { copy(displayShowNever = showDoNotShowAgain) }
  }
  action(FeedbackModel.showLater()) { showLater() }
  action(FeedbackModel.showNever()) { showNever() }
  action(FeedbackModel.openReddit()) {
    navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
    navigator.pop(key)
  }
  action(FeedbackModel.sendMail()) {
    navigator.push(FeedbackMailKey)
    navigator.pop(key)
  }
}

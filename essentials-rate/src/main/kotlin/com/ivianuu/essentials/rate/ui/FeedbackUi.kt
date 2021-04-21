package com.ivianuu.essentials.rate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object FeedbackKey : DialogKey<Nothing>

@Given
val feedbackUi: ModelKeyUi<FeedbackKey, FeedbackModel> = {
    DialogScaffold(dismissible = false) {
        Dialog(
            title = { Text(stringResource(R.string.es_feedback_title)) },
            content = { Text(stringResource(R.string.es_feedback_content)) },
            neutralButton = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (model.displayShowNever) {
                        TextButton(onClick = model.showNever) {
                            Text(stringResource(R.string.es_never))
                        }
                    }
                    TextButton(onClick = model.showLater) {
                        Text(stringResource(R.string.es_later))
                    }
                }
            },
            negativeButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    TextButton(onClick = model.openReddit) {
                        Text(stringResource(R.string.es_open_reddit))
                    }
                    TextButton(onClick = model.sendMail) {
                        Text(stringResource(R.string.es_send_mail))
                    }
                }
            }
        )
    }
}

@Optics
data class FeedbackModel(
    val displayShowNever: Boolean = false,
    val showNever: () -> Unit = {},
    val showLater: () -> Unit = {},
    val openReddit: () -> Unit = {},
    val sendMail: () -> Unit = {},
)

@Given
fun feedbackModel(
    @Given displayShowNever: DisplayShowNeverUseCase,
    @Given navigator: Navigator,
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given showLater: ShowLaterUseCase,
    @Given showNever: ShowNeverUseCase
): @Scoped<KeyUiGivenScope> StateFlow<FeedbackModel> = scope.state(FeedbackModel()) {
    launch {
        val showDoNotShowAgain = displayShowNever()
        update { copy(displayShowNever = showDoNotShowAgain) }
    }
    action(FeedbackModel.showLater()) { showLater() }
    action(FeedbackModel.showNever()) { showNever() }
    action(FeedbackModel.openReddit()) {
        navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
    }
    action(FeedbackModel.sendMail()) { navigator.push(FeedbackMailKey) }
}

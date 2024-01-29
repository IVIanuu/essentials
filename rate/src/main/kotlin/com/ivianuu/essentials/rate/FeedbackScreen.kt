/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import androidx.compose.material.Text
import androidx.compose.runtime.produceState
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.dialog.DialogScreen
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlScreen
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

object FeedbackScreen : DialogScreen<Unit>

@Provide val feedbackUi = Ui<FeedbackScreen, FeedbackState> { state ->
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(stringResource(R.string.es_feedback_title)) },
      content = { Text(stringResource(R.string.es_feedback_content)) },
      buttons = {
        if (state.displayShowNever) {
          TextButton(onClick = state.showNever) {
            Text(stringResource(R.string.es_never))
          }
        }
        TextButton(onClick = state.showLater) {
          Text(stringResource(R.string.es_later))
        }

        TextButton(onClick = state.openReddit) {
          Text(stringResource(R.string.es_open_reddit))
        }
        TextButton(onClick = state.sendMail) {
          Text(stringResource(R.string.es_send_mail))
        }
      }
    )
  }
}

data class FeedbackState(
  val displayShowNever: Boolean,
  val showNever: () -> Unit,
  val showLater: () -> Unit,
  val openReddit: () -> Unit,
  val sendMail: () -> Unit
)

@Provide fun feedbackPresenter(
  navigator: Navigator,
  rateUseCases: RateUseCases,
  screen: FeedbackScreen
) = Presenter {
  FeedbackState(
    displayShowNever = produceState(false) { value = rateUseCases.shouldDisplayShowNever() }.value,
    showNever = action { rateUseCases.showNever() },
    showLater = action { rateUseCases.showLater() },
    openReddit = action {
      navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
      navigator.pop(screen)
    },
    sendMail = action {
      navigator.push(FeedbackMailScreen)
      navigator.pop(screen)
    }
  )
}

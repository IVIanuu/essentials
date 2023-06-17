/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import androidx.compose.runtime.produceState
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.RateUseCases
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.dialog.DialogScreen
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlScreen
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

object FeedbackScreen : DialogScreen<Unit>

@Provide val feedbackUi = Ui<FeedbackScreen, FeedbackModel> { model ->
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(R.string.es_feedback_title) },
      content = { Text(R.string.es_feedback_content) },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) {
            Text(R.string.es_never)
          }
        }
        TextButton(onClick = model.showLater) {
          Text(R.string.es_later)
        }

        TextButton(onClick = model.openReddit) {
          Text(R.string.es_open_reddit)
        }
        TextButton(onClick = model.sendMail) {
          Text(R.string.es_send_mail)
        }
      }
    )
  }
}

data class FeedbackModel(
  val displayShowNever: Boolean,
  val showNever: () -> Unit,
  val showLater: () -> Unit,
  val openReddit: () -> Unit,
  val sendMail: () -> Unit
)

@Provide fun feedbackModel(
  navigator: Navigator,
  rateUseCases: RateUseCases,
  screen: FeedbackScreen
) = Model {
  FeedbackModel(
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

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.RateUseCases
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.produce
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.PopupKey
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

object FeedbackKey : PopupKey<Unit>

@Provide val feedbackUi = ModelKeyUi<FeedbackKey, FeedbackModel> {
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(R.string.es_feedback_title) },
      content = { Text(R.string.es_feedback_content) },
      buttons = {
        if (displayShowNever) {
          TextButton(onClick = showNever) {
            Text(R.string.es_never)
          }
        }
        TextButton(onClick = showLater) {
          Text(R.string.es_later)
        }

        TextButton(onClick = openReddit) {
          Text(R.string.es_open_reddit)
        }
        TextButton(onClick = sendMail) {
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

context(KeyUiContext<FeedbackKey>, RateUseCases) @Provide fun feedbackModel() = Model {
  FeedbackModel(
    displayShowNever = produce(false) { shouldDisplayShowNever() },
    showNever = action { showNever() },
    showLater = action { showLater() },
    openReddit = action {
      navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
      navigator.pop(key)
    },
    sendMail = action {
      navigator.push(FeedbackMailKey)
      navigator.pop(key)
    }
  )
}

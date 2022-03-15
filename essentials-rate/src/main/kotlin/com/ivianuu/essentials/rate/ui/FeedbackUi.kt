/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

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

@Provide fun feedbackModel(
  displayShowNever: DisplayShowNeverUseCase,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase,
  ctx: KeyUiContext<FeedbackKey>
) = Model {
  FeedbackModel(
    displayShowNever = produce(false) { displayShowNever() },
    showNever = action(block = showNever),
    showLater = action(block = showLater),
    openReddit = action {
      ctx.navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
      ctx.navigator.pop(ctx.key)
    },
    sendMail = action {
      ctx.navigator.push(FeedbackMailKey)
      ctx.navigator.pop(ctx.key)
    }
  )
}

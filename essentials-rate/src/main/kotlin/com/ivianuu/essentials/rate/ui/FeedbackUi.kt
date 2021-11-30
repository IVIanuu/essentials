/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.DisplayShowNeverUseCase
import com.ivianuu.essentials.rate.domain.ShowLaterUseCase
import com.ivianuu.essentials.rate.domain.ShowNeverUseCase
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.produceValue
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Provide

object FeedbackKey : DialogKey<Unit>

@Provide val feedbackUi = ModelKeyUi<FeedbackKey, FeedbackModel> {
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

@Provide @Composable fun feedbackModel(
  displayShowNever: DisplayShowNeverUseCase,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase,
  ctx: KeyUiContext<FeedbackKey>
) = FeedbackModel(
  displayShowNever = produceValue(false) { displayShowNever() },
  showNever = action(showNever),
  showLater = action(showLater),
  openReddit = action {
    ctx.navigator.push(UrlKey("https://www.reddit.com/r/manuelwrageapps"))
    ctx.navigator.pop(ctx.key)
  },
  sendMail = action {
    ctx.navigator.push(FeedbackMailKey)
    ctx.navigator.pop(ctx.key)
  }
)

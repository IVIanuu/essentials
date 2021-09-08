/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.DisplayShowNeverUseCase
import com.ivianuu.essentials.rate.domain.ShowLaterUseCase
import com.ivianuu.essentials.rate.domain.ShowNeverUseCase
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object FeedbackKey : DialogKey<Unit>

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
  scope: InjektCoroutineScope<KeyUiScope>,
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

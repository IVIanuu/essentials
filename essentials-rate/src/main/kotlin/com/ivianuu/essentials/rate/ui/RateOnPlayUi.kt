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
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.DisplayShowNeverUseCase
import com.ivianuu.essentials.rate.domain.RateOnPlayUseCase
import com.ivianuu.essentials.rate.domain.ShowLaterUseCase
import com.ivianuu.essentials.rate.domain.ShowNeverUseCase
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.state.action
import com.ivianuu.essentials.ui.state.produceValue
import com.ivianuu.injekt.Provide

object RateOnPlayKey : DialogKey<Unit>

@Provide val rateOnPlayUi: ModelKeyUi<RateOnPlayKey, RateOnPlayModel> = {
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(R.string.es_rate_on_play_title) },
      content = { Text(R.string.es_rate_on_play_content) },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) {
            Text(R.string.es_never)
          }
        }

        TextButton(onClick = model.showLater) {
          Text(R.string.es_later)
        }

        TextButton(onClick = model.rate) {
          Text(R.string.es_rate)
        }
      }
    )
  }
}

data class RateOnPlayModel(
  val displayShowNever: Boolean,
  val rate: () -> Unit,
  val showLater: () -> Unit,
  val showNever: () -> Unit,
)

@Provide fun rateOnPlayModel(
  displayShowNever: DisplayShowNeverUseCase,
  rateOnPlay: RateOnPlayUseCase,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase,
  ctx: KeyUiContext<RateOnPlayKey>
): @Composable () -> RateOnPlayModel = {
  RateOnPlayModel(
    displayShowNever = produceValue(false) { displayShowNever() },
    rate = action {
      rateOnPlay()
      ctx.navigator.pop(ctx.key)
    },
    showLater = action(showLater),
    showNever = action(showNever)
  )
}

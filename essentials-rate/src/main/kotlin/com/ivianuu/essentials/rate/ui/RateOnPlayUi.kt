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
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.DisplayShowNeverUseCase
import com.ivianuu.essentials.rate.domain.RateOnPlayUseCase
import com.ivianuu.essentials.rate.domain.ShowLaterUseCase
import com.ivianuu.essentials.rate.domain.ShowNeverUseCase
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.produce
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogKey
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.StateFlow

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

@Optics data class RateOnPlayModel(
  val displayShowNever: Boolean = false,
  val rate: () -> Unit = {},
  val showLater: () -> Unit = {},
  val showNever: () -> Unit = {},
)

@Provide fun rateOnPlayModel(
  displayShowNever: DisplayShowNeverUseCase,
  key: RateOnPlayKey,
  navigator: Navigator,
  rateOnPlay: RateOnPlayUseCase,
  scope: NamedCoroutineScope<KeyUiScope>,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase
): StateFlow<RateOnPlayModel> = scope.state(RateOnPlayModel()) {
  produce({ copy(displayShowNever = it) }) { displayShowNever() }

  action(RateOnPlayModel.showLater()) { showLater() }
  action(RateOnPlayModel.showNever()) { showNever() }
  action(RateOnPlayModel.rate()) {
    rateOnPlay()
    navigator.pop(key)
  }
}

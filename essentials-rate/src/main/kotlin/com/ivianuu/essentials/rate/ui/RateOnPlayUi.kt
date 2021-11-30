/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.DisplayShowNeverUseCase
import com.ivianuu.essentials.rate.domain.RateOnPlayUseCase
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
import com.ivianuu.injekt.Provide

object RateOnPlayKey : DialogKey<Unit>

@Provide val rateOnPlayUi = ModelKeyUi<RateOnPlayKey, RateOnPlayModel> {
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

@Provide @Composable fun rateOnPlayModel(
  displayShowNever: DisplayShowNeverUseCase,
  rateOnPlay: RateOnPlayUseCase,
  showLater: ShowLaterUseCase,
  showNever: ShowNeverUseCase,
  ctx: KeyUiContext<RateOnPlayKey>
) = RateOnPlayModel(
  displayShowNever = produceValue(false) { displayShowNever() },
  rate = action {
    rateOnPlay()
    ctx.navigator.pop(ctx.key)
  },
  showLater = action(showLater),
  showNever = action(showNever)
)

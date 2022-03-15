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

object RateOnPlayKey : PopupKey<Unit>

@Provide val rateOnPlayUi = ModelKeyUi<RateOnPlayKey, RateOnPlayModel> {
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(R.string.es_rate_on_play_title) },
      content = { Text(R.string.es_rate_on_play_content) },
      buttons = {
        if (displayShowNever) {
          TextButton(onClick = showNever) { Text(R.string.es_never) }
        }

        TextButton(onClick = showLater) { Text(R.string.es_later) }

        TextButton(onClick = rate) { Text(R.string.es_rate) }
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
) = Model {
  RateOnPlayModel(
    displayShowNever = produce(false) { displayShowNever() },
    rate = action {
      rateOnPlay()
      ctx.navigator.pop(ctx.key)
    },
    showLater = action(block = showLater),
    showNever = action(block = showNever)
  )
}

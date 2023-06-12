/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.ui

import androidx.compose.material.Text
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.produce
import com.ivianuu.essentials.rate.R
import com.ivianuu.essentials.rate.domain.RateUseCases
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.dialog.DialogScaffold
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.OverlayKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

object RateOnPlayKey : OverlayKey<Unit>

@Provide val rateOnPlayUi = KeyUi<RateOnPlayKey, RateOnPlayModel> { model ->
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(R.string.es_rate_on_play_title) },
      content = { Text(R.string.es_rate_on_play_content) },
      buttons = {
        if (model.displayShowNever) {
          TextButton(onClick = model.showNever) { Text(R.string.es_never) }
        }

        TextButton(onClick = model.showLater) { Text(R.string.es_later) }

        TextButton(onClick = model.rate) { Text(R.string.es_rate) }
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
  ctx: KeyUiContext<RateOnPlayKey>,
  rateUseCases: RateUseCases
) = Model {
  RateOnPlayModel(
    displayShowNever = produce(false) { rateUseCases.shouldDisplayShowNever() },
    rate = action {
      rateUseCases.rateOnPlay()
      ctx.navigator.pop(ctx.key)
    },
    showLater = action { rateUseCases.showLater() },
    showNever = action { rateUseCases.showNever() }
  )
}

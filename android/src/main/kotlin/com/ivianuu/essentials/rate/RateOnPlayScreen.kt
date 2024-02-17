/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

object RateOnPlayScreen : DialogScreen<Unit>

@Provide val rateOnPlayUi = Ui<RateOnPlayScreen, RateOnPlayState> { state ->
  DialogScaffold(dismissible = false) {
    Dialog(
      title = { Text(stringResource(R.string.rate_on_play_title)) },
      content = { Text(stringResource(R.string.rate_on_play_content)) },
      buttons = {
        if (state.displayShowNever) {
          TextButton(onClick = state.showNever) { Text(stringResource(R.string.never)) }
        }

        TextButton(onClick = state.showLater) { Text(stringResource(R.string.later)) }

        TextButton(onClick = state.rate) { Text(stringResource(R.string.rate)) }
      }
    )
  }
}

data class RateOnPlayState(
  val displayShowNever: Boolean,
  val rate: () -> Unit,
  val showLater: () -> Unit,
  val showNever: () -> Unit,
)

@Provide fun rateOnPlayPresenter(
  navigator: Navigator,
  rateUseCases: RateUseCases,
  screen: RateOnPlayScreen
) = Presenter {
  RateOnPlayState(
    displayShowNever = produceState(false) { value = rateUseCases.shouldDisplayShowNever() }.value,
    rate = action {
      rateUseCases.rateOnPlay()
      navigator.pop(screen)
    },
    showLater = action { rateUseCases.showLater() },
    showNever = action { rateUseCases.showNever() }
  )
}

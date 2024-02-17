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

class RateOnPlayScreen : DialogScreen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      rateUseCases: RateUseCases,
      screen: RateOnPlayScreen
    ) = Ui<RateOnPlayScreen, Unit> {
      DialogScaffold(dismissible = false) {
        Dialog(
          title = { Text(stringResource(R.string.rate_on_play_title)) },
          content = { Text(stringResource(R.string.rate_on_play_content)) },
          buttons = {
            if (produceState(false) { value = rateUseCases.shouldDisplayShowNever() }.value) {
              TextButton(onClick = action {
                rateUseCases.rateOnPlay()
                navigator.pop(screen)
              }) {
                Text(stringResource(R.string.never))
              }
            }

            TextButton(onClick =  action { rateUseCases.showLater() }) {
              Text(stringResource(R.string.later))
            }

            TextButton(onClick = action { rateUseCases.showNever() }) {
              Text(stringResource(R.string.rate))
            }
          }
        )
      }
    }
  }
}

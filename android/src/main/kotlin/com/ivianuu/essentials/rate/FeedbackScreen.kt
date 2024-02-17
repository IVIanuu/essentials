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

class FeedbackScreen : DialogScreen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      rateUseCases: RateUseCases,
      screen: FeedbackScreen
    ) = Ui<FeedbackScreen, Unit> {
      DialogScaffold(dismissible = false) {
        Dialog(
          title = { Text(stringResource(R.string.feedback_title)) },
          content = { Text(stringResource(R.string.feedback_content)) },
          buttons = {
            if (produceState(false) { value = rateUseCases.shouldDisplayShowNever() }.value) {
              TextButton(onClick = action { rateUseCases.showNever() }) {
                Text(stringResource(R.string.never))
              }
            }
            TextButton(onClick = action { rateUseCases.showLater() }) {
              Text(stringResource(R.string.later))
            }

            TextButton(onClick = action {
              navigator.push(UrlScreen("https://www.reddit.com/r/manuelwrageapps"))
              navigator.pop(screen)
            }) {
              Text(stringResource(R.string.open_reddit))
            }
            TextButton(onClick = action {
              navigator.push(FeedbackMailScreen())
              navigator.pop(screen)
            }) {
              Text(stringResource(R.string.send_mail))
            }
          }
        )
      }
    }
  }
}

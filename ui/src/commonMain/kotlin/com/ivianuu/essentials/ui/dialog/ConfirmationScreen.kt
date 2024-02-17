/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class ConfirmationScreen(val title: String) : DialogScreen<ConfirmationScreen.Result> {
  enum class Result { CONFIRMED, DENIED }
}

@Provide fun confirmationUi(
  navigator: Navigator,
  screen: ConfirmationScreen,
) = Ui<ConfirmationScreen, Unit> {
  DialogScaffold {
    Dialog(
      title = { Text(screen.title) },
      buttons = {
        ConfirmationScreen.Result.entries.forEach { item ->
          TextButton(onClick = action { navigator.pop(screen, item) }) {
            Text(item.name)
          }
        }
      }
    )
  }
}

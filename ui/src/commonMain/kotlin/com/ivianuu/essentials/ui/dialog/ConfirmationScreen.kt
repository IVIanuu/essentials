/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

class ConfirmationScreen(val title: String) : DialogScreen<ConfirmationScreen.Result> {
  enum class Result { CONFIRMED, DENIED }

  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: ConfirmationScreen,
    ) = Ui<ConfirmationScreen> {
      Dialog(
        title = { Text(screen.title) },
        buttons = {
          TextButton(onClick = scopedAction { navigator.pop(screen, Result.DENIED) }) {
            Text("Deny")
          }

          TextButton(onClick = scopedAction { navigator.pop(screen, Result.CONFIRMED) }) {
            Text("Confirm")
          }
        }
      )
    }
  }
}

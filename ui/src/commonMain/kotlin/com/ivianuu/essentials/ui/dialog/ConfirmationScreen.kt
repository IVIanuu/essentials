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
      AlertDialog(
        onDismissRequest = action { navigator.pop(screen, null) },
        title = { Text(screen.title) },
        confirmButton = {
          TextButton(onClick = action { navigator.pop(screen, Result.CONFIRMED) }) {
            Text("Confirm")
          }
        },
        dismissButton = {
          TextButton(onClick = action { navigator.pop(screen, Result.DENIED) }) {
            Text("Deny")
          }
        },
      )
    }
  }
}

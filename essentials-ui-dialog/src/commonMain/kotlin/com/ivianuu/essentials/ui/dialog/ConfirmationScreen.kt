/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class ConfirmationScreen<T : Any>(
  val items: List<T>,
  val title: String,
  @Inject val renderer: UiRenderer<T>,
) : OverlayScreen<T>

@Provide fun confirmationUi(
  key: ConfirmationScreen<Any>,
  navigator: Navigator
) = Ui<ConfirmationScreen<Any>, Unit> {
  DialogScaffold {
    Dialog(
      title = { Text(key.title) },
      buttons = {
        key.items.forEach { item ->
          TextButton(onClick = action { navigator.pop(key, item) }) {
            Text(key.renderer(item))
          }
        }
      }
    )
    }
  }

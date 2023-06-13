/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.material.Text
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.OverlayKey
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

class ConfirmationKey<T : Any>(
  val items: List<T>,
  val title: String,
  @Inject val renderer: UiRenderer<T>,
) : OverlayKey<T>

@Provide fun confirmationUi(ctx: KeyUiContext<ConfirmationKey<Any>>) =
  Ui<ConfirmationKey<Any>, Unit> { model ->
    DialogScaffold {
      Dialog(
        title = { Text(ctx.key.title) },
        buttons = {
          ctx.key.items.forEach { item ->
            TextButton(onClick = action { ctx.navigator.pop(ctx.key, item) }) {
              Text(ctx.key.renderer(item))
            }
          }
        }
      )
    }
  }

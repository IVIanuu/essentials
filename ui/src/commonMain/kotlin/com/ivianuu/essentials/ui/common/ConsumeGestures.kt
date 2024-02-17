/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.*

fun Modifier.consumeGestures(consume: Boolean = true): Modifier = if (!consume) this
else pointerInput(Unit) {
  awaitPointerEventScope {
    // we should wait for all new pointer events
    while (true) {
      awaitPointerEvent(pass = PointerEventPass.Initial)
        .changes
        .forEach {
          if (it.pressed != it.previousPressed)
            it.consume()
        }
    }
  }
}

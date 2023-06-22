/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.consumeGestures(consume: Boolean = true): Modifier {
  return if (!consume) this
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
}

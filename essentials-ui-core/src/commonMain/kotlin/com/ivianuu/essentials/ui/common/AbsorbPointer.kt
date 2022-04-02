/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.absorbPointer(enabled: Boolean = true): Modifier = composed {
  pointerInput(enabled) {
    if (enabled) {
      forEachGesture {
        awaitPointerEventScope {
          while (true) {
            awaitPointerEvent(PointerEventPass.Initial)
              .changes
              .forEach { it.consumeDownChange() }
          }
        }
      }
    }
  }
}

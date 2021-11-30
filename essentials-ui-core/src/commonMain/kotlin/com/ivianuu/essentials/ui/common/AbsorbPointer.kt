/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.*

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

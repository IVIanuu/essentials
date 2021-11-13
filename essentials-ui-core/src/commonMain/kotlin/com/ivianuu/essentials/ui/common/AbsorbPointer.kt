/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

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

package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.gestures.action.Action

@Composable fun ActionIcon(action: Action<*>, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier,
    propagateMinConstraints = true,
    contentAlignment = Alignment.Center
  ) {
    action.icon.collectAsState(null).value?.invoke()
  }
}

val LocalActionIconSizeModifier = staticCompositionLocalOf { Modifier.size(24.dp) }
val LocalActionImageSizeModifier = staticCompositionLocalOf { Modifier.size(40.dp) }

val LocalActionIconRotation = staticCompositionLocalOf { 0f }
val LocalActionIconPosition = staticCompositionLocalOf { ActionIconPosition.LEFT }

enum class ActionIconPosition {
  LEFT, RIGHT
}

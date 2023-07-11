/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
    action.icon()
  }
}

val LocalActionIconSizeModifier = staticCompositionLocalOf { Modifier.size(24.dp) }
val LocalActionImageSizeModifier = staticCompositionLocalOf { Modifier.size(40.dp) }

val LocalActionIconRotation = staticCompositionLocalOf { 0f }
val LocalActionIconPosition = staticCompositionLocalOf { ActionIconPosition.LEFT }

enum class ActionIconPosition { LEFT, RIGHT }

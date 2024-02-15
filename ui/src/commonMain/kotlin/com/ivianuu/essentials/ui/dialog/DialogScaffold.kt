/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.LocalScope
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.screen
import com.slack.circuit.foundation.internal.BackHandler
import kotlinx.coroutines.launch

@Composable fun DialogScaffold(
  modifier: Modifier = Modifier,
  dismissible: Boolean = true,
  onDismissRequest: () -> Unit = defaultDismissRequestHandler,
  dialog: @Composable () -> Unit,
) {
  if (!dismissible) {
    BackHandler { }
  }

  Box(
    modifier = Modifier
      .animationElement(DialogScrimKey)
      .pointerInput(true) {
        detectTapGestures { onDismissRequest() }
      }
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .then(modifier),
    contentAlignment = Alignment.Center
  ) {
    InsetsPadding(
      modifier = Modifier
        .animationElement(DialogKey)
        .pointerInput(true) { detectTapGestures { } }
        .wrapContentSize(align = Alignment.Center)
        .padding(all = 32.dp)
    ) {
      dialog()
    }
  }
}

private val defaultDismissRequestHandler: () -> Unit
  @Composable get() {
    val navigator = LocalScope.current.navigator
    val key = LocalScope.current.screen
    val scope = rememberCoroutineScope()
    return { scope.launch { navigator.pop(key) } }
  }

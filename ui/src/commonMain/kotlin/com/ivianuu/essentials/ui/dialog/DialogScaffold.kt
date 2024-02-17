/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.navigation.*
import com.slack.circuit.foundation.internal.*
import kotlinx.coroutines.*

@Composable fun DialogScaffold(
  modifier: Modifier = Modifier,
  dismissible: Boolean = true,
  onDismissRequest: () -> Unit = defaultDismissRequestHandler,
  dialog: @Composable () -> Unit,
) {
  if (!dismissible)
    BackHandler { }

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

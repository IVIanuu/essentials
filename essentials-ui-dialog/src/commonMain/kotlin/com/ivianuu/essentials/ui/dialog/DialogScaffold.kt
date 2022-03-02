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
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.backpress.*
import com.ivianuu.essentials.ui.insets.*

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
      .animationElement(ScrimAnimationElementKey)
      .pointerInput(true) {
        detectTapGestures { onDismissRequest() }
      }
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .then(modifier),
    contentAlignment = Alignment.Center
  ) {
    InsetsPadding {
      Box(
        modifier = Modifier
          .pointerInput(true) { detectTapGestures { } }
          .wrapContentSize(align = Alignment.Center)
          .animationElement(PopupAnimationElementKey)
          .padding(all = 32.dp),
        contentAlignment = Alignment.Center
      ) {
        dialog()
      }
    }
  }
}

private val defaultDismissRequestHandler: () -> Unit
  @Composable get() {
    val backHandler = LocalBackPressHandler.current
    return { backHandler.back() }
  }

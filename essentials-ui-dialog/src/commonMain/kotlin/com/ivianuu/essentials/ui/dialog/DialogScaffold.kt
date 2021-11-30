/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.animation.transition.PopupAnimationElementKey
import com.ivianuu.essentials.ui.animation.transition.ScrimAnimationElementKey
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.essentials.ui.backpress.LocalBackPressHandler
import com.ivianuu.essentials.ui.insets.InsetsPadding

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

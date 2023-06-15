/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.push

@Composable fun PopupMenuButton(
  modifier: Modifier = Modifier,
  onCancel: (() -> Unit)? = null,
  popupContent: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .size(size = 40.dp)
      .popupClickable(
        indication = rememberRipple(bounded = false),
        onCancel = onCancel,
        popupContent = popupContent
      )
      .then(modifier),
    contentAlignment = Alignment.Center
  ) {
    Icon(Icons.Default.MoreVert, null)
  }
}

@Composable fun Modifier.popupClickable(
  indication: Indication = LocalIndication.current,
  onCancel: (() -> Unit)? = null,
  popupContent: @Composable () -> Unit
) = composed {
  val navigator = LocalScope.current.navigator

  var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

  onGloballyPositioned { coordinates = it }
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = indication,
      onClick = action {
        val windowCoords = coordinates!!.findRootCoordinates()
        val boundsInWindow = coordinates!!.boundsInWindow()
        val isLeft = (boundsInWindow.center.x < windowCoords.size.width / 2)
        val isTop = (boundsInWindow.center.y <
            windowCoords.size.height - (windowCoords.size.height / 10))

        navigator.push(
          PopupScreen(
            position = coordinates!!.boundsInRoot(),
            transformOrigin = TransformOrigin(
              if (isLeft) 0f else 1f,
              if (isTop) 0f else 1f
            ),
            onCancel = onCancel
          ) {
            Popup(popupContent)
          }
        )
      }
    )
}


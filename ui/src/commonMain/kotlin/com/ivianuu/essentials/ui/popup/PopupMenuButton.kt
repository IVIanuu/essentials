/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.*

@Composable fun PopupMenuButton(
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  enabled: Boolean = true,
  onCancel: (() -> Unit)? = null,
  positionPicker: (LayoutCoordinates, Offset) -> Rect = { coordinates, _ -> coordinates.boundsInRoot() },
  popupContent: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .minimumInteractiveComponentSize()
      .popupClickable(
        interactionSource = interactionSource,
        indication = rememberRipple(bounded = false, radius = 24.dp),
        enabled = enabled,
        onCancel = onCancel,
        positionPicker = positionPicker
      ) {
        PopupContainer {
          popupContent()
        }
      },
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.MoreVert,
      contentDescription = null
    )
  }
}

@Composable fun Modifier.popupClickable(
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  indication: Indication? = LocalIndication.current,
  enabled: Boolean = true,
  onCancel: (() -> Unit)? = null,
  positionPicker: (LayoutCoordinates, Offset) -> Rect = { coordinates, _ -> coordinates.boundsInRoot() },
  popupContent: @Composable () -> Unit
): Modifier {
  val navigator = LocalScope.current.navigator
  var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

  val scope = rememberCoroutineScope()
  return onGloballyPositioned { coordinates = it }
    .clickableWithPosition(
      interactionSource = interactionSource,
      indication = indication,
      enabled = enabled
    ) { position ->
      scope.launch(start = CoroutineStart.UNDISPATCHED) {
        val windowCoords = coordinates!!.findRootCoordinates()
        val boundsInWindow = coordinates!!.boundsInWindow()
        val isLeft = windowCoords.localPositionOf(coordinates!!, position).x < windowCoords.size.width / 2
        val isTop = boundsInWindow.center.y <
            windowCoords.size.height - (windowCoords.size.height / 10)

        navigator.push(
          PopupScreen(
            position = positionPicker(coordinates!!, position),
            transformOrigin = TransformOrigin(
              if (isLeft) 0f else 1f,
              if (isTop) 0f else 1f
            ),
            onCancel = onCancel
          ) {
            popupContent()
          }
        )
      }
    }
}

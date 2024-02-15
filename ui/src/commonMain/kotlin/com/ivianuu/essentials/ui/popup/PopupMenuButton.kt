/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.LocalScope
import com.ivianuu.essentials.ui.common.clickableWithPosition
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.push
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

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
        val isLeft = (windowCoords.localPositionOf(coordinates!!, position).x < windowCoords.size.width / 2)
        val isTop = (boundsInWindow.center.y <
            windowCoords.size.height - (windowCoords.size.height / 10))

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

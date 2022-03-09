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
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.*

@Composable fun PopupMenuButton(
  items: List<PopupMenu.Item>,
  onCancel: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = Modifier
      .size(size = 40.dp)
      .popupClickable(
        items = items,
        onCancel = onCancel,
        indication = rememberRipple(bounded = false)
      )
      .then(modifier),
    contentAlignment = Alignment.Center
  ) {
    Icon(Icons.Default.MoreVert, null)
  }
}

@Composable fun Modifier.popupClickable(
  items: List<PopupMenu.Item>,
  onCancel: (() -> Unit)? = null,
  indication: Indication = LocalIndication.current,
) = composed {
  val component = LocalUiElements.current<PopupMenuButtonComponent>()

  var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

  val scope = rememberCoroutineScope()
  onGloballyPositioned { coordinates = it }
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = indication
    ) {
      scope.launch {
        component.navigator.push(
          PopupKey(
            position = coordinates!!.boundsInRoot(),
            onCancel = onCancel
          ) {
            PopupMenu(items = items)
          }
        )
      }
    }
}

@Provide @Element<UiScope>
data class PopupMenuButtonComponent(val navigator: Navigator)

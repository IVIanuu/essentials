/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.ivianuu.essentials.*

class DropdownMenuState(initialStatus: Status = Status.Closed) {
  var status: Status by mutableStateOf(initialStatus)
  sealed interface Status {
    data class Open(val offset: DpOffset = DpOffset.Zero) : Status
    data object Closed : Status
  }
}

@Composable fun DropdownMenu(
  state: DropdownMenuState,
  modifier: Modifier = Modifier,
  scrollState: ScrollState = rememberScrollState(),
  properties: PopupProperties = remember { PopupProperties(focusable = true) },
  content: @Composable DropdownMenuScope.() -> Unit
) {
  DropdownMenu(
    modifier = modifier,
    expanded = state.status is DropdownMenuState.Status.Open,
    onDismissRequest = { state.status = DropdownMenuState.Status.Closed },
    offset = state.status.safeAs<DropdownMenuState.Status.Open>()?.offset ?: DpOffset.Zero,
    scrollState = scrollState,
    properties = properties
  ) {
    content(
      remember(state, this) {
        object : DropdownMenuScope, ColumnScope by this {
          override fun dismiss() {
            state.status = DropdownMenuState.Status.Closed
          }
        }
      }
    )
  }
}

interface DropdownMenuScope : ColumnScope {
  fun dismiss()
}

@Composable fun DropdownMenuScope.DropdownMenuItem(
  onClick: () -> Unit,
  dismissOnClick: Boolean = true,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable RowScope.() -> Unit
) = androidx.compose.material.DropdownMenuItem(
  onClick = {
    if (dismissOnClick) dismiss()
    onClick()
  },
  modifier = modifier,
  enabled = enabled,
  contentPadding = contentPadding,
  interactionSource = interactionSource,
  content = content
)

@Composable fun DropdownMenuButton(
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  indication: Indication? = ripple(bounded = false, radius = 24.dp),
  enabled: Boolean = true,
  dropdownContent: @Composable DropdownMenuScope.() -> Unit
) {
  val dropdownState = remember { DropdownMenuState() }

  Box(
    modifier = modifier
      .minimumInteractiveComponentSize()
      .clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled
      ) { dropdownState.status = DropdownMenuState.Status.Open() },
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.MoreVert,
      contentDescription = null
    )

    DropdownMenu(state = dropdownState, content = dropdownContent)
  }
}

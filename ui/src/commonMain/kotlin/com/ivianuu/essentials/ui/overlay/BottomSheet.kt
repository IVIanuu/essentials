/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.overlay

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.scopedAction
import com.ivianuu.essentials.ui.material.EsModalBottomSheet
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import injekt.Provide

@Composable fun BottomSheetLauncherButton(
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  indication: Indication? = ripple(bounded = false, radius = 24.dp),
  enabled: Boolean = true,
  menuContent: @Composable BottomSheetScope.() -> Unit
) {
  val navigator = LocalScope.current.navigator
  Box(
    modifier = modifier
      .minimumInteractiveComponentSize()
      .clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onClick = scopedAction {
          navigator.push(BottomSheetScreen(menuContent))
        }
      ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.MoreVert,
      contentDescription = null
    )
  }
}

interface BottomSheetScope : ColumnScope {
  val sheetState: SheetState
  fun dismiss()
}

class BottomSheetScreen(
  val content: @Composable BottomSheetScope.() -> Unit
) : OverlayScreen<Unit>

@Provide fun bottomSheetUi(
  navigator: Navigator,
  screen: BottomSheetScreen
) = Ui<BottomSheetScreen> {
  val sheetState = rememberModalBottomSheetState()

  val hideAndDismissAction = action {
    sheetState.hide()
    if (!sheetState.isVisible)
      navigator.pop(screen)
  }

  EsModalBottomSheet(sheetState = sheetState) {
    screen.content(
      object : BottomSheetScope, ColumnScope by this {
        override val sheetState: SheetState
          get() = sheetState

        override fun dismiss() {
          hideAndDismissAction()
        }
      }
    )
  }
}

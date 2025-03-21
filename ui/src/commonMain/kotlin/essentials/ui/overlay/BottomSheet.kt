/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

@Composable fun BottomSheetLauncherButton(
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  indication: Indication? = ripple(bounded = false, radius = 24.dp),
  enabled: Boolean = true,
  navigator: Navigator = inject,
  menuContent: @Composable BottomSheetScope.(@Provide ScreenContext<BottomSheetScreen>) -> Unit
) {
  Box(
    modifier = modifier
      .minimumInteractiveComponentSize()
      .clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onClick = action { navigator.push(BottomSheetScreen(menuContent)) }
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
  val state: BottomSheetState
  fun dismiss()
}

class BottomSheetScreen(
  val content: @Composable BottomSheetScope.(
    @Provide ScreenContext<BottomSheetScreen>
  ) -> Unit
) : OverlayScreen<Unit>

@Provide @Composable fun BottomSheetUi(
  context: ScreenContext<BottomSheetScreen> = inject
): Ui<BottomSheetScreen> {
  val state = rememberBottomSheetState()

  val hideAndDismissAction = action {
    state.animateTo(BottomSheetValue.HIDDEN)
    popWithResult<Unit>()
  }

  BottomSheet(state = state) {
    context.screen.content(
      object : BottomSheetScope, ColumnScope by this {
        override val state get() = state
        override fun dismiss() = hideAndDismissAction()
      },
      context
    )
  }
}

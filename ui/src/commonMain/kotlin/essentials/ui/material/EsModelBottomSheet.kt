package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import essentials.compose.*
import essentials.ui.navigation.*
import injekt.*

@Composable fun EsModalBottomSheet(
  modifier: Modifier = Modifier,
  sheetState: SheetState = rememberModalBottomSheetState(),
  sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
  shape: Shape = BottomSheetDefaults.ExpandedShape,
  containerColor: Color = BottomSheetDefaults.ContainerColor,
  contentColor: Color = contentColorFor(containerColor),
  tonalElevation: Dp = 0.dp,
  scrimColor: Color = BottomSheetDefaults.ScrimColor,
  dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
  contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
  properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
  onDismissRequest: onBottomSheetDismissRequest = inject,
  content: @Composable ColumnScope.() -> Unit,
) {
  ModalBottomSheet(
    onDismissRequest = action(block = onDismissRequest),
    modifier = modifier.statusBarsPadding(),
    sheetState = sheetState,
    sheetMaxWidth = sheetMaxWidth,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    scrimColor = scrimColor,
    dragHandle = dragHandle,
    contentWindowInsets = contentWindowInsets,
    properties = properties,
    content = content
  )
}

@Tag typealias onBottomSheetDismissRequest = suspend () -> Unit
@Provide fun defaultOnBottomSheetDismissRequest(
  navigator: Navigator = inject
): onBottomSheetDismissRequest = { navigator.popTop() }

package essentials.ui.material

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import essentials.LocalScope
import essentials.compose.action
import essentials.ui.navigation.navigator
import essentials.ui.navigation.popTop

@Composable fun EsModalBottomSheet(
  onDismissRequest: () -> Unit = run {
    val navigator = LocalScope.current.navigator
    action { navigator.popTop() }
  },
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
  content: @Composable ColumnScope.() -> Unit,
) {
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
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

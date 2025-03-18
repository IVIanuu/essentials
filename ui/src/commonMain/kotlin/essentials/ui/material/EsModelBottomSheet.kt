package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
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
  dragHandle: @Composable (() -> Unit)? = {
    BottomSheetDefaults.DragHandle(
      color = MaterialTheme.colorScheme.onSurfaceVariant
        .copy(alpha = ContentAlpha.Medium)
    )
  },
  contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
  contentPadding: PaddingValues = PaddingValues(8.dp),
  properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
  onDismissRequest: onBottomSheetDismissRequest = inject,
  content: @Composable ColumnScope.() -> Unit,
) {
  val registry = LocalSaveableStateRegistry.current
  ModalBottomSheet(
    onDismissRequest = action(block = onDismissRequest),
    modifier = modifier,
    sheetState = sheetState,
    sheetMaxWidth = sheetMaxWidth,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    scrimColor = scrimColor,
    dragHandle = dragHandle,
    contentWindowInsets = contentWindowInsets,
    properties = properties
  ) {
    Column(modifier = Modifier.padding(contentPadding)) {
      CompositionLocalProvider(LocalSaveableStateRegistry provides registry) {
        content()
      }
    }
  }
}

@Tag typealias onBottomSheetDismissRequest = suspend () -> Unit
@Provide fun defaultOnBottomSheetDismissRequest(
  navigator: Navigator = inject
): onBottomSheetDismissRequest = { navigator.popTop() }

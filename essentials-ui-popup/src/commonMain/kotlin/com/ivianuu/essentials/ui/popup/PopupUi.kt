package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Constraints
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.refOf
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.layout.systemBarsPadding
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide

class PopupScreen(
  val position: Rect,
  val onCancel: (() -> Unit)?,
  val content: @Composable () -> Unit,
) : OverlayScreen<Unit>

@Provide fun popupUi(navigator: Navigator, screen: PopupScreen) = Ui<PopupScreen, Unit> {
  var previousConstraints by remember { refOf<Constraints?>(null) }

  BoxWithConstraints {
    if (previousConstraints != null && constraints != previousConstraints)
      LaunchedEffect(true) {
        navigator.pop(screen)
      }

    previousConstraints = constraints

    var dismissed by remember { refOf(false) }

    val dismiss: (Boolean) -> Unit = action { cancelled ->
      if (!dismissed) {
        dismissed = true
        navigator.pop(screen)
        if (cancelled) screen.onCancel?.invoke()
      }
    }

    PopupLayout(
      position = screen.position,
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(true) {
          detectTapGestures { dismiss(true) }
        }
    ) {
      Box(
        modifier = Modifier
          .pointerInput(true) {
            detectTapGestures {
            }
          }
      ) {
        screen.content()
      }
    }
  }
}

@Provide val popupScreenConfig: ScreenConfig<PopupScreen>
  get() = ScreenConfig(opaque = true)

@Composable private fun PopupLayout(
  position: Rect,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  var globalLayoutPosition by remember { mutableStateOf(Offset.Zero) }
  Layout(
    content = content,
    modifier = modifier
      .systemBarsPadding()
      .onGloballyPositioned { globalLayoutPosition = it.positionInRoot() }
  ) { measureables, constraints ->
    val childConstraints = constraints.copy(
      minWidth = 0,
      minHeight = 0,
      maxWidth = constraints.maxWidth,
      maxHeight = constraints.maxHeight
    )

    val placeable = measureables.single().measure(childConstraints)

    var y = position.top.toInt() - globalLayoutPosition.y.toInt()
    var x: Int

    // Find the ideal horizontal position.
    if ((position.left + position.right / 2) < constraints.maxWidth / 2) {
      x = position.left.toInt()
    } else if (position.left < position.right) {
      x = (position.right - placeable.width).toInt()
    } else {
      x = (position.right - placeable.width).toInt()
    }

    x = x/*.coerceIn(
      insets.left.insetOrMinPadding(),
      max(
        insets.left.insetOrMinPadding(),
        constraints.maxWidth -
            placeable.width -
            insets.right.insetOrMinPadding()
      )
    )*/
    y = y/*.coerceIn(
      insets.top.insetOrMinPadding(),
      max(
        insets.top.insetOrMinPadding(),
        constraints.maxHeight -
            placeable.height -
            insets.bottom.insetOrMinPadding()
      )
    )*/

    layout(constraints.maxWidth, constraints.maxHeight) {
      placeable.place(x = x, y = y)
    }
  }
}

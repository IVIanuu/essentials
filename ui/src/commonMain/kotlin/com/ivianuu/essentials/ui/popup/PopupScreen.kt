package com.ivianuu.essentials.ui.popup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import soup.compose.material.motion.MotionConstants.DefaultFadeInDuration
import soup.compose.material.motion.animation.*
import kotlin.math.*

class PopupScreen(
  val position: Rect,
  val transformOrigin: TransformOrigin,
  val onCancel: (() -> Unit)?,
  val content: @Composable () -> Unit,
) : OverlayScreen<Unit>

@Provide fun popupUi(navigator: Navigator, screen: PopupScreen) = Ui<PopupScreen, Unit> {
  var previousConstraints by remember { mutableStateOf<Constraints?>(null) }

  BoxWithConstraints {
    if (previousConstraints != null && constraints != previousConstraints)
      LaunchedEffect(true) {
        navigator.pop(screen)
      }

    previousConstraints = constraints

    var dismissed by remember { mutableStateOf(false) }

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
          .animationElement(PopupKey)
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

private val PopupKey = "popup"

@Provide val popupScreenConfig: ScreenConfig<PopupScreen>
  get() = ScreenConfig(opaque = true) {
    if (isPush) {
      PopupKey entersWith
          fadeIn(
            animationSpec = tween(
              durationMillis = DefaultFadeInDuration.ForFade,
              easing = LinearEasing
            )
          ) + scaleIn(
        animationSpec = tween(
          durationMillis = DefaultFadeInDuration,
          easing = FastOutSlowInEasing
        ),
        initialScale = 0.8f,
        transformOrigin = target!!.cast<PopupScreen>().transformOrigin
      )
    } else {
      PopupKey exitsWith materialFadeOut()
    }
  }

@Composable private fun PopupLayout(
  position: Rect,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  val insets = WindowInsets.safeContent
  var globalLayoutPosition by remember { mutableStateOf(Offset.Zero) }
  Layout(
    content = content,
    modifier = modifier
      .onGloballyPositioned { globalLayoutPosition = it.positionInRoot() }
  ) { measureables, constraints ->
    fun Int.insetOrMinPadding() = max(this, 16.dp.roundToPx())

    val childConstraints = constraints.copy(
      minWidth = 0,
      minHeight = 0,
      maxWidth = constraints.maxWidth -
          insets.getLeft(this, layoutDirection).insetOrMinPadding() -
          insets.getRight(this, layoutDirection).insetOrMinPadding(),
      maxHeight = constraints.maxHeight -
          insets.getTop(this).insetOrMinPadding() -
          insets.getBottom(this).insetOrMinPadding()
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

    x = x.coerceIn(
      insets.getLeft(this, layoutDirection).insetOrMinPadding(),
      max(
        insets.getLeft(this, layoutDirection).insetOrMinPadding(),
        constraints.maxWidth -
            placeable.width -
            insets.getRight(this, layoutDirection).insetOrMinPadding()
      )
    )
    y = y.coerceIn(
      insets.getTop(this).insetOrMinPadding(),
      max(
        insets.getTop(this).insetOrMinPadding(),
        constraints.maxHeight -
            placeable.height -
            insets.getBottom(this).insetOrMinPadding()
      )
    )

    layout(constraints.maxWidth, constraints.maxHeight) {
      placeable.place(x = x, y = y)
    }
  }
}

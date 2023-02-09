/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.refOf
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.animation.transition.FadeScaleStackTransition
import com.ivianuu.essentials.ui.animation.transition.PopupAnimationElementKey
import com.ivianuu.essentials.ui.animation.transition.StackTransition
import com.ivianuu.essentials.ui.animation.transition.awaitLayoutCoordinates
import com.ivianuu.essentials.ui.animation.transition.fromElementModifier
import com.ivianuu.essentials.ui.animation.transition.rootCoordinates
import com.ivianuu.essentials.ui.animation.transition.toElementModifier
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.SimpleKeyUi
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.injekt.Provide
import kotlin.math.max

data class PopupKey(
  val position: Rect,
  val onCancel: (() -> Unit)?,
  val content: @Composable () -> Unit,
) : com.ivianuu.essentials.ui.navigation.PopupKey<Unit>

@Provide fun popupUi(key: PopupKey, navigator: Navigator) = SimpleKeyUi<PopupKey> {
  var previousConstraints by remember { refOf<Constraints?>(null) }

  BoxWithConstraints {
    if (previousConstraints != null && constraints != previousConstraints)
      LaunchedEffect(true) {
        navigator.pop(key)
      }

    previousConstraints = constraints

    var dismissed by remember { refOf(false) }

    val dismiss: (Boolean) -> Unit = action { cancelled ->
      if (!dismissed) {
        dismissed = true
        navigator.pop(key)
        if (cancelled) key.onCancel?.invoke()
      }
    }

    PopupLayout(
      position = key.position,
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(true) {
          detectTapGestures { dismiss(true) }
        }
    ) {
      Box(
        modifier = Modifier
          .animationElement(PopupAnimationElementKey)
          .pointerInput(true) {
            detectTapGestures {
            }
          }
      ) {
        key.content()
      }
    }
  }
}

@Provide val popupKeyOptionsFactory = KeyUiOptionsFactory<PopupKey> { _, _, _ ->
  KeyUiOptions(opaque = true, transition = PopupStackTransition)
}

val PopupStackTransition: StackTransition = transition@ {
  val popupModifier = (if (isPush) toElementModifier(PopupAnimationElementKey)
  else fromElementModifier(PopupAnimationElementKey))
    ?: return@transition
  if (isPush) {
    attachTo()
    popupModifier.value = Modifier.alpha(0f)
    val popupLayoutCoords = popupModifier.awaitLayoutCoordinates()
    val windowCoords = popupLayoutCoords.rootCoordinates
    val boundsInWindow = popupLayoutCoords.boundsInWindow()
    val isLeft = (boundsInWindow.center.x < windowCoords.size.width / 2)
    val isTop = (boundsInWindow.center.y <
        windowCoords.size.height - (windowCoords.size.height / 10))
    popupModifier.value = Modifier
    FadeScaleStackTransition(
      TransformOrigin(
        if (isLeft) 0f else 1f,
        if (isTop) 0f else 1f
      )
    )()
  } else {
    FadeScaleStackTransition()()
  }
}

@Composable private fun PopupLayout(
  position: Rect,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  val insets = LocalInsets.current
  Layout(content = content, modifier = modifier) { measureables, constraints ->
    fun Dp.insetOrMinPadding() = max(this, 16.dp).roundToPx()

    val childConstraints = constraints.copy(
      minWidth = 0,
      minHeight = 0,
      maxWidth = constraints.maxWidth -
          insets.left.insetOrMinPadding() -
          insets.right.insetOrMinPadding(),
      maxHeight = constraints.maxHeight -
          insets.top.insetOrMinPadding() -
          insets.bottom.insetOrMinPadding()
    )

    val placeable = measureables.single().measure(childConstraints)

    var y = position.top.toInt()
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
      insets.left.insetOrMinPadding(),
      max(
        insets.left.insetOrMinPadding(),
        constraints.maxWidth -
            placeable.width -
            insets.right.insetOrMinPadding()
      )
    )
    y = y.coerceIn(
      insets.top.insetOrMinPadding(),
      max(
        insets.top.insetOrMinPadding(),
        constraints.maxHeight -
            placeable.height -
            insets.bottom.insetOrMinPadding()
      )
    )

    layout(constraints.maxWidth, constraints.maxHeight) {
      placeable.place(x = x, y = y)
    }
  }
}

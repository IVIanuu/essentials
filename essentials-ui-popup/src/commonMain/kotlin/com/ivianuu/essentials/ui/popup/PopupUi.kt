/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.unit.Constraints
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.animation.transition.FadeScaleStackTransition
import com.ivianuu.essentials.ui.animation.transition.PopupAnimationElementKey
import com.ivianuu.essentials.ui.animation.transition.StackTransition
import com.ivianuu.essentials.ui.animation.transition.awaitLayoutCoordinates
import com.ivianuu.essentials.ui.animation.transition.fromElementModifier
import com.ivianuu.essentials.ui.animation.transition.rootCoordinates
import com.ivianuu.essentials.ui.animation.transition.toElementModifier
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class PopupKey(
  val position: Rect,
  val onCancel: (() -> Unit)?,
  val content: @Composable () -> Unit,
) : Key<Unit>

@Provide fun popupUi(key: PopupKey, navigator: Navigator) = KeyUi<PopupKey> {
  var previousConstraints by remember { refOf<Constraints?>(null) }

  BoxWithConstraints {
    if (previousConstraints != null && constraints != previousConstraints)
      LaunchedEffect(true) {
        navigator.pop(key)
      }

    previousConstraints = constraints

    var dismissed by remember { refOf(false) }

    val scope = rememberCoroutineScope()

    val dismiss: (Boolean) -> Unit = { cancelled ->
      if (!dismissed) {
        dismissed = true
        scope.launch { navigator.pop(key) }
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

@Provide val popupKeyOptionsFactory = KeyUiOptionsFactory<PopupKey> {
  KeyUiOptions(opaque = true, transition = PopupStackTransition)
}

val PopupStackTransition: StackTransition = transition@{
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
    )(this)
  } else {
    FadeScaleStackTransition()(this)
  }
}

@Composable private fun PopupLayout(
  position: Rect,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  val insets = LocalInsets.current
  Layout(content = content, modifier = modifier) { measureables, constraints ->
    val childConstraints = constraints.copy(
      minWidth = 0,
      minHeight = 0
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
      insets.left.roundToPx(),
      constraints.maxWidth -
          placeable.width -
          insets.right.roundToPx()
    )
    y = y.coerceIn(
      insets.top.roundToPx(),
      constraints.maxHeight -
          placeable.height -
          insets.bottom.roundToPx()
    )

    layout(constraints.maxWidth, constraints.maxHeight) {
      placeable.place(x = x, y = y)
    }
  }
}

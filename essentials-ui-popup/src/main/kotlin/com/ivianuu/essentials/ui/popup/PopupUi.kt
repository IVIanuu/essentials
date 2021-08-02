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

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.concurrent.*
import kotlin.time.*

data class PopupKey(
  val position: Rect,
  val onCancel: (() -> Unit)?,
  val content: @Composable () -> Unit,
) : Key<Nothing>

@Provide fun popupUi(key: PopupKey, navigator: Navigator): KeyUi<PopupKey> = {
  val configuration = LocalConfiguration.current
  val initialConfiguration = remember { configuration }
  val scope = rememberCoroutineScope()
  if (configuration !== initialConfiguration) {
    scope.launch { navigator.pop(key) }
  }

  var dismissed by remember { refOf(false) }

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

@Provide val popupKeyOptionsFactory: KeyUiOptionsFactory<PopupKey> = {
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

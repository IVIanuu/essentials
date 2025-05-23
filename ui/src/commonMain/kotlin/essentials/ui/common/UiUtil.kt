/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import essentials.coroutines.*
import injekt.*

fun Modifier.interactive(
  interactive: Boolean,
  animateAlpha: Boolean = true
): Modifier = composed {
  val targetAlpha = if (interactive) 1f else 0.5f
  alpha(
    alpha = if (animateAlpha) animateFloatAsState(targetAlpha).value
    else targetAlpha
  )
    .consumeGestures(!interactive)
}

fun Modifier.consumeGestures(consume: Boolean = true): Modifier = if (!consume) this
else pointerInput(Unit) {
  awaitPointerEventScope {
    // we should wait for all new pointer events
    while (true) {
      awaitPointerEvent(pass = PointerEventPass.Initial)
        .changes
        .fastForEach {
          if (it.pressed != it.previousPressed)
            it.consume()
        }
    }
  }
}

@Composable fun ProvideContentColorTextStyle(
  contentColor: Color,
  textStyle: TextStyle,
  content: @Composable () -> Unit
) {
  val mergedStyle = LocalTextStyle.current.merge(textStyle)
  CompositionLocalProvider(
    LocalContentColor provides contentColor,
    LocalTextStyle provides mergedStyle,
    content = content
  )
}

fun Modifier.animatePlacement(
  animationSpec: FiniteAnimationSpec<IntOffset> = spring(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
  )
): Modifier = composed {
  @Provide val scope = rememberCoroutineScope()
  var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
  var animatable by remember {
    mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
  }
  onPlaced {
    // Calculate the position in the parent layout
    targetOffset = it.positionInParent().round()
  }.offset {
    // Animate to the new target offset when alignment changes.
    val anim = animatable
      ?: Animatable(targetOffset, IntOffset.VectorConverter)
        .also { animatable = it }
    if (anim.targetValue != targetOffset) {
      launch {
        anim.animateTo(targetOffset, animationSpec)
      }
    }
    // Offset the child in the opposite direction to the targetOffset, and slowly catch
    // up to zero offset via an animation to achieve an overall animated movement.
    animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
  }
}

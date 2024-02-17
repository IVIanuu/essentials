package com.ivianuu.essentials.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

fun Modifier.animatePlacement(
  animationSpec: FiniteAnimationSpec<IntOffset> = spring(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
  )
): Modifier = composed {
  val scope = rememberCoroutineScope()
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
      scope.launch {
        anim.animateTo(targetOffset, animationSpec)
      }
    }
    // Offset the child in the opposite direction to the targetOffset, and slowly catch
    // up to zero offset via an animation to achieve an overall animated movement.
    animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
  }
}

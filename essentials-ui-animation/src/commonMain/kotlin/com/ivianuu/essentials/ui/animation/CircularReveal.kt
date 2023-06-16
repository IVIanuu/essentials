package com.ivianuu.essentials.ui.animation

/**
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.circularReveal(
  visible: Boolean,
  center: (fullSize: Size) -> Offset = { Offset.Unspecified },
  transitionSpec: @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
): Modifier = composed(
  inspectorInfo = debugInspectorInfo {
    name = "circularReveal"
    properties["visible"] = visible
    properties["center"] = center
  }
) {
  val transition = updateTransition(targetState = visible, label = "circularReveal")
  val progress: Float by transition.animateFloat(
    transitionSpec = transitionSpec,
    label = "progress",
    targetValueByState = { circularRevealVisible -> if (circularRevealVisible) 1f else 0f }
  )
  graphicsLayer {
    clip = progress < 1f
    shape = CircularRevealShape(
      progress = progress,
      center = center,
    )
  }
}

private class CircularRevealShape(
  @FloatRange(from = 0.0, to = 1.0)
  private val progress: Float,
  private val center: (fullSize: Size) -> Offset = { Offset.Unspecified },
) : Shape {

  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density,
  ): Outline {
    val center: Offset = center(size).takeOrElse { size.center }
    val radius: Float = size.getLongestRadiusFrom(center) * progress
    val path = Path().also {
      it.addOval(oval = Rect(center = center, radius = radius))
    }
    return Outline.Generic(path = path)
  }

  private fun Size.getLongestRadiusFrom(center: Offset): Float {
    val topLeft: Float = hypot(center.x, center.y)
    val topRight: Float = hypot(width - center.x, center.y)
    val bottomLeft: Float = hypot(center.x, height - center.y)
    val bottomRight: Float = hypot(width - center.x, height - center.y)
    return maxOf(topLeft, topRight, bottomLeft, bottomRight)
  }
}
*/

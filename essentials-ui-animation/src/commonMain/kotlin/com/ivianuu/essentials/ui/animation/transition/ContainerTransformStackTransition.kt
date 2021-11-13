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

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalAbsoluteElevation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.ivianuu.essentials.coroutines.parTupled
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.ui.animation.AnimationElementPropKey
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.animation.util.interval

fun ContainerTransformStackTransition(
  closedKey: Any,
  openedKey: Any,
  spec: AnimationSpec<Float> = defaultAnimationSpec(),
): StackTransition = {
  val fromModifier = fromElementModifier(if (isPush) closedKey else openedKey)!!
  val toModifier = toElementModifier(if (isPush) openedKey else closedKey)!!

  toModifier.value = Modifier.alpha(0f)

  attachTo()

  val (fromBounds, toBounds) = parTupled(
    { fromModifier.awaitLayoutCoordinates().boundsInWindow() },
    { toModifier.awaitLayoutCoordinates().boundsInWindow() }
  )

  val startPosition = fromBounds.topLeft
  val endPosition = toBounds.topLeft

  val startWidth = fromBounds.width
  val startHeight = fromBounds.height
  val endWidth = toBounds.width
  val endHeight = toBounds.height

  var currentFraction by mutableStateOf(0f)
  var currentScrimAlpha by mutableStateOf(0f)
  var currentPosition by mutableStateOf(startPosition)
  var currentWidth by mutableStateOf(startWidth)
  var currentHeight by mutableStateOf(startHeight)
  var currentColor by mutableStateOf(Color.Transparent)
  var currentCornerSize by mutableStateOf(0.dp)
  var currentBorderWidth by mutableStateOf(0.dp)
  var currentBorderColor by mutableStateOf(Color.Transparent)
  var currentElevation by mutableStateOf(0.dp)
  var currentAbsoluteElevation by mutableStateOf(0.dp)
  var currentFromAlpha by mutableStateOf(1f)
  var currentToAlpha by mutableStateOf(0f)

  val fromProps =
    fromElement(if (isPush) closedKey else openedKey)!![ContainerTransformPropsKey]!!
  val toProps = toElement(if (isPush) openedKey else closedKey)!![ContainerTransformPropsKey]!!

  overlay {
    Box(
      modifier = Modifier.fillMaxSize()
        .background(Color.Black.copy(alpha = 0.54f * currentScrimAlpha))
    ) {
      CompositionLocalProvider(
        LocalAbsoluteElevation provides currentAbsoluteElevation
      ) {
        Surface(
          modifier = Modifier
            .composed {
              with(LocalDensity.current) {
                size(currentWidth.toDp(), currentHeight.toDp())
                  .graphicsLayer {
                    translationX = currentPosition.x
                    translationY = currentPosition.y
                  }
              }
            },
          shape = RoundedCornerShape(currentCornerSize),
          color = currentColor,
          border = if (currentBorderWidth > 0.dp) BorderStroke(
            currentBorderWidth,
            currentBorderColor
          ) else null,
          elevation = currentElevation
        ) {
          CompositionLocalProvider(fromProps.compositionLocalContext) {
            Box(
              modifier = Modifier
                .then(object : LayoutModifier {
                  override fun MeasureScope.measure(
                    measurable: Measurable,
                    constraints: Constraints
                  ): MeasureResult {
                    val placeable = measurable.measure(
                      Constraints.fixed(
                        fromBounds.width.toInt(), fromBounds.height.toInt()
                      )
                    )
                    return layout(currentWidth.toInt(), currentHeight.toInt()) {
                      placeable.place(0, 0)
                    }
                  }
                })
                .graphicsLayer {
                  alpha = currentFromAlpha
                  val sourceSize = Size(
                    fromBounds.width,
                    fromBounds.width * currentHeight / currentWidth
                  )
                  val destinationSize = Size(
                    currentWidth,
                    sourceSize.height * currentWidth / sourceSize.width
                  )
                  transformOrigin = TransformOrigin(0f, 0f)
                  scaleX = destinationSize.width / sourceSize.width
                  scaleY = destinationSize.height / sourceSize.height
                },
            ) {
              CompositionLocalProvider(
                LocalContainerTransformTransitionFraction provides
                    if (isPush) currentFraction else 1f - currentFraction,
                content = fromProps.content
              )
            }
          }
          CompositionLocalProvider(toProps.compositionLocalContext) {
            Box(
              modifier = Modifier
                .then(object : LayoutModifier {
                  override fun MeasureScope.measure(
                    measurable: Measurable,
                    constraints: Constraints
                  ): MeasureResult {
                    val placeable = measurable.measure(
                      Constraints.fixed(
                        toBounds.width.toInt(), toBounds.height.toInt()
                      )
                    )
                    return layout(currentWidth.toInt(), currentHeight.toInt()) {
                      placeable.place(0, 0)
                    }
                  }
                })
                .graphicsLayer {
                  alpha = currentToAlpha
                  val sourceSize = Size(
                    toBounds.width,
                    toBounds.width * currentHeight / currentWidth
                  )
                  val destinationSize = Size(
                    currentWidth,
                    sourceSize.height * currentWidth / sourceSize.width
                  )
                  transformOrigin = TransformOrigin(0f, 0f)
                  scaleX = destinationSize.width / sourceSize.width
                  scaleY = destinationSize.height / sourceSize.height
                },
            ) {
              CompositionLocalProvider(
                LocalContainerTransformTransitionFraction provides
                    if (isPush) 1f - currentFraction else currentFraction,
                content = toProps.content
              )
            }
          }
        }
      }
    }
  }

  animate(spec) { value ->
    fromModifier.value = Modifier.alpha(0f)
    currentFraction = value
    currentScrimAlpha = if (isPush) {
      interval(0f, 0.3f, value)
    } else {
      lerp(1f, 0f, FastOutSlowInEasing.transform(value))
    }
    currentPosition = lerp(startPosition, endPosition, FastOutSlowInEasing.transform(value))
    currentWidth = lerp(startWidth, endWidth, FastOutSlowInEasing.transform(value))
    currentHeight = lerp(startHeight, endHeight, FastOutSlowInEasing.transform(value))
    currentColor = lerp(fromProps.color, toProps.color, value)
    currentCornerSize = lerp(fromProps.cornerSize, toProps.cornerSize, value)
    currentBorderWidth = lerp(fromProps.borderWidth, toProps.borderWidth, value)
    currentBorderColor = lerp(fromProps.borderColor, toProps.borderColor, value)
    currentElevation = lerp(fromProps.elevation, toProps.elevation, value)
    currentAbsoluteElevation = lerp(fromProps.absoluteElevation, toProps.absoluteElevation, value)
    currentToAlpha = interval(0.2f, 0.4f, value)
    if (!isPush) {
      currentFromAlpha = lerp(1f, 0f, interval(0.15f, 0.45f, value))
    }
  }
}

val ContainerTransformPropsKey = AnimationElementPropKey<ContainerTransformProps>()

class ContainerTransformProps(
  compositionLocalContext: CompositionLocalContext,
  content: @Composable () -> Unit,
  color: Color,
  cornerSize: Dp,
  borderWidth: Dp,
  borderColor: Color,
  elevation: Dp,
  absoluteElevation: Dp
) {
  var compositionLocalContext by mutableStateOf(compositionLocalContext)
  var content by mutableStateOf(content)
  var color by mutableStateOf(color)
  var cornerSize by mutableStateOf(cornerSize)
  var borderWidth by mutableStateOf(borderWidth)
  var borderColor by mutableStateOf(borderColor)
  var elevation by mutableStateOf(elevation)
  var absoluteElevation by mutableStateOf(absoluteElevation)
}

val LocalContainerTransformTransitionFraction = compositionLocalOf { 0f }

@Composable fun ContainerTransformSurface(
  key: Any,
  isOpened: Boolean,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colors.surface,
  cornerSize: Dp = 0.dp,
  borderWidth: Dp = 0.dp,
  borderColor: Color = Color.Transparent,
  elevation: Dp = 0.dp,
  content: @Composable () -> Unit = {},
) {
  val compositionLocalContext = currentCompositionLocalContext
  val absoluteElevation = LocalAbsoluteElevation.current
  val props = remember {
    ContainerTransformProps(
      compositionLocalContext, content, color,
      cornerSize, borderWidth, borderColor, elevation, absoluteElevation
    )
  }
  SideEffect {
    props.content = content
    props.cornerSize = cornerSize
    props.color = color
    props.borderWidth = borderWidth
    props.borderColor = borderColor
    props.elevation = elevation
    props.absoluteElevation = absoluteElevation
  }
  Surface(
    modifier = modifier
      .animationElement(key, ContainerTransformPropsKey to props),
    color = color,
    shape = RoundedCornerShape(cornerSize),
    border = if (borderWidth > 0.dp) BorderStroke(borderWidth, borderColor) else null,
    elevation = elevation
  ) {
    CompositionLocalProvider(
      LocalContainerTransformTransitionFraction provides if (isOpened) 1f else 0f
    ) {
      val compositionLocalContext = currentCompositionLocalContext
      SideEffect {
        props.compositionLocalContext = compositionLocalContext
      }
      content()
    }
  }
}

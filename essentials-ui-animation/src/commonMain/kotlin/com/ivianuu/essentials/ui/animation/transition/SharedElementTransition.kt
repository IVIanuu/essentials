/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.AnimationElement
import com.ivianuu.essentials.ui.animation.AnimationElementPropKey
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import com.ivianuu.essentials.ui.animation.animationElement
import com.ivianuu.essentials.ui.animation.util.arcLerp
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlin.time.Duration

fun SharedElementStackTransition(
  vararg sharedElements: Pair<Any, Any>,
  sharedElementAnimationSpec: AnimationSpec<Float> = defaultAnimationSpec(easing = FastOutSlowInEasing),
  contentTransition: StackTransition = CrossFadeStackTransition(sharedElementAnimationSpec),
  waitingTimeout: Duration = 200.milliseconds
): StackTransition = {
  val states = sharedElements
    .map { (from, to) ->
      if (isPush) {
        SharedElementState(from, fromElement(from), fromElementModifier(from)) to
            SharedElementState(to, toElement(to), toElementModifier(to))
      } else {
        SharedElementState(to, fromElement(to), fromElementModifier(to)) to
            SharedElementState(from, toElement(from), toElementModifier(from))
      }
    }

  // hide "to" content while waiting for shared elements
  val toContentModifier = toElementModifier(ContentAnimationElementKey)
  toContentModifier?.value = Modifier.alpha(0f)
  // if we do not yield here some items bounds do not appear
  yield()
  attachTo()

  // wait until all shared elements has been placed on the screen
  withTimeoutOrNull(waitingTimeout) {
    while (true) {
      delay(1)
      if (states.all { (start, end) ->
          start.bounds != null &&
              end.bounds != null
        }) break
    }
  }

  // capture geometry
  states
    .filter { it.first.bounds != null && it.second.bounds != null }
    .forEach { (start, end) ->
      val startBounds = start.bounds!!
      val endBounds = end.bounds!!
      start.capturedGeometry = SharedElementGeometry(
        position = Offset(
          x = startBounds.left + (startBounds.width - endBounds.width) / 2,
          y = startBounds.top + (startBounds.height - endBounds.height) / 2
        ),
        scaleX = startBounds.width / endBounds.width,
        scaleY = startBounds.height / endBounds.height,
        fraction = 0f
      )
      end.capturedGeometry = SharedElementGeometry(
        position = Offset(x = endBounds.left, y = endBounds.top),
        scaleX = 1f,
        scaleY = 1f,
        fraction = 1f
      )
    }

  // install overlay
  overlay {
    states
      .filter { it.first.bounds != null && it.second.bounds != null }
      .forEach { (_, endState) ->
        val endBounds = endState.bounds!!
        val currentGeometry = endState.animatedGeometry!!
        val sharedElementProps = endState.element!![SharedElementPropsKey]!!
        key(endState) {
          Box(
            modifier = Modifier
              .then(object : LayoutModifier {
                override fun MeasureScope.measure(
                  measurable: Measurable,
                  constraints: Constraints
                ): MeasureResult {
                  val placeable = measurable.measure(
                    Constraints.fixed(
                      endBounds.width.toInt(), endBounds.height.toInt()
                    )
                  )
                  return layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                  }
                }
              })
              .graphicsLayer {
                scaleX = currentGeometry.scaleX
                scaleY = currentGeometry.scaleY
                translationX = currentGeometry.position.x
                translationY = currentGeometry.position.y
              }
          ) {
            CompositionLocalProvider(sharedElementProps.compositionLocalContext) {
              CompositionLocalProvider(
                LocalSharedElementTransitionFraction provides endState.animatedGeometry!!.fraction,
                content = sharedElementProps.content
              )
            }
          }
        }
      }
  }
  // show the content
  toContentModifier?.value = Modifier

  par(
    {
      contentTransition(
        object : StackTransitionScope by this {
          override fun attachTo() {
          }

          override fun detachFrom() {
          }
        }
      )
    },
    {
      animate(sharedElementAnimationSpec) { value ->
        states
          .filter { it.first.capturedGeometry != null && it.second.capturedGeometry != null }
          .forEach { (start, end) ->
            end.modifier?.value = Modifier.alpha(0f)
            start.modifier?.value = Modifier.alpha(0f)
            end.animatedGeometry = SharedElementGeometry(
              position = arcLerp(
                if (isPush) start.capturedGeometry!!.position else end.capturedGeometry!!.position,
                if (isPush) end.capturedGeometry!!.position else start.capturedGeometry!!.position,
                if (isPush) value else 1f - value
              ),
              scaleX = lerp(
                start.capturedGeometry!!.scaleX,
                end.capturedGeometry!!.scaleX,
                value
              ),
              scaleY = lerp(
                start.capturedGeometry!!.scaleY,
                end.capturedGeometry!!.scaleY,
                value
              ),
              fraction = if (isPush) value else 1f - value
            )
          }
      }
    }
  )
}

val LocalSharedElementTransitionFraction = compositionLocalOf { 0f }

private data class SharedElementState(
  val key: Any?,
  val element: AnimationElement?,
  val modifier: MutableState<Modifier>?
) {
  var bounds: Rect? = null
    private set
  var animatedGeometry: SharedElementGeometry? by mutableStateOf(null)
  var capturedGeometry: SharedElementGeometry? = null

  init {
    updateModifier()
  }

  private fun updateModifier() {
    modifier?.value = Modifier
      .onGloballyPositioned { coords ->
        if (bounds == null) {
          bounds = coords
            .takeIf { it.isAttached }
            ?.boundsInRoot()
        }
      }
  }
}

private data class SharedElementGeometry(
  val fraction: Float,
  val position: Offset,
  val scaleX: Float,
  val scaleY: Float
)

private class SharedElementProps(
  compositionLocalContext: CompositionLocalContext,
  content: @Composable () -> Unit
) {
  var compositionLocalContext by mutableStateOf(compositionLocalContext)
  var content by mutableStateOf(content)
}

private val SharedElementPropsKey = AnimationElementPropKey<SharedElementProps>()

@Composable fun SharedElement(
  modifier: Modifier = Modifier,
  key: Any,
  isStart: Boolean,
  content: @Composable () -> Unit
) {
  val compositionLocalContext = currentCompositionLocalContext
  val props = remember { SharedElementProps(compositionLocalContext, content) }
  props.compositionLocalContext = compositionLocalContext
  props.content = content
  Box(
    modifier = modifier
      .animationElement(key, SharedElementPropsKey to props)
  ) {
    CompositionLocalProvider(
      LocalSharedElementTransitionFraction provides if (isStart) 0f else 1f,
      content = content
    )
  }
}

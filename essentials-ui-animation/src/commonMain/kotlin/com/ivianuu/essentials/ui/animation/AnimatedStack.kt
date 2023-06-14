@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER", "INVISIBLE_SETTER")

package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.createDeferredAnimation
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.ivianuu.essentials.ui.animation.AnimatedStackScope.SlideDirection.Companion.Down
import com.ivianuu.essentials.ui.animation.AnimatedStackScope.SlideDirection.Companion.End
import com.ivianuu.essentials.ui.animation.AnimatedStackScope.SlideDirection.Companion.Left
import com.ivianuu.essentials.ui.animation.AnimatedStackScope.SlideDirection.Companion.Right
import com.ivianuu.essentials.ui.animation.AnimatedStackScope.SlideDirection.Companion.Start
import com.ivianuu.essentials.ui.animation.AnimatedStackScope.SlideDirection.Companion.Up

@Composable
fun <T> AnimatedStack(
  targetState: List<T>,
  modifier: Modifier = Modifier,
  transitionSpec: AnimatedStackScope<T>.() -> ContentTransform = {
    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) with
        fadeOut(animationSpec = tween(90))
  },
  contentAlignment: Alignment = Alignment.TopStart,
  contentKey: (T) -> Any? = { it },
  contentOpaque: (T) -> Boolean = { false },
  label: String = "AnimatedStack",
  content: @Composable AnimatedVisibilityScope.(targetState: T) -> Unit
) {
  val transition = updateTransition(targetState = targetState, label = label)
  transition.AnimatedStack(
    modifier,
    transitionSpec,
    contentAlignment,
    contentKey,
    contentOpaque,
    content
  )
}

class AnimatedStackScope<T> internal constructor(
  internal val transition: Transition<List<T>>,
  internal var contentAlignment: Alignment,
  internal var layoutDirection: LayoutDirection
) : Transition.Segment<List<T>> {
  override val initialState: List<T>
    @Suppress("UnknownNullness")
    get() = transition.segment.initialState

  override val targetState: List<T>
    @Suppress("UnknownNullness")
    get() = transition.segment.targetState

  infix fun ContentTransform.using(sizeTransform: SizeTransform?) = this.apply {
    this.sizeTransform = sizeTransform
  }

  @Immutable
  @JvmInline
  value class SlideDirection internal constructor(private val value: Int) {
    companion object {
      val Left = SlideDirection(0)
      val Right = SlideDirection(1)
      val Up = SlideDirection(2)
      val Down = SlideDirection(3)
      val Start = SlideDirection(4)
      val End = SlideDirection(5)
    }

    override fun toString(): String {
      return when (this) {
        Left -> "Left"
        Right -> "Right"
        Up -> "Up"
        Down -> "Down"
        Start -> "Start"
        End -> "End"
        else -> "Invalid"
      }
    }
  }

  fun slideIntoContainer(
    towards: SlideDirection,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
      visibilityThreshold = IntOffset.VisibilityThreshold
    ),
    initialOffset: (offsetForFullSlide: Int) -> Int = { it }
  ): EnterTransition =
    when {
      towards.isLeft -> slideInHorizontally(animationSpec) {
        initialOffset.invoke(
          currentSize.width - calculateOffset(IntSize(it, it), currentSize).x
        )
      }
      towards.isRight -> slideInHorizontally(animationSpec) {
        initialOffset.invoke(-calculateOffset(IntSize(it, it), currentSize).x - it)
      }
      towards == Up -> slideInVertically(animationSpec) {
        initialOffset.invoke(
          currentSize.height - calculateOffset(IntSize(it, it), currentSize).y
        )
      }
      towards == Down -> slideInVertically(animationSpec) {
        initialOffset.invoke(-calculateOffset(IntSize(it, it), currentSize).y - it)
      }
      else -> EnterTransition.None
    }

  private val SlideDirection.isLeft: Boolean
    get() {
      return this == Left || this == Start && layoutDirection == LayoutDirection.Ltr ||
          this == End && layoutDirection == LayoutDirection.Rtl
    }

  private val SlideDirection.isRight: Boolean
    get() {
      return this == Right || this == Start && layoutDirection == LayoutDirection.Rtl ||
          this == End && layoutDirection == LayoutDirection.Ltr
    }

  private fun calculateOffset(fullSize: IntSize, currentSize: IntSize): IntOffset {
    return contentAlignment.align(fullSize, currentSize, LayoutDirection.Ltr)
  }

  fun slideOutOfContainer(
    towards: SlideDirection,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
      visibilityThreshold = IntOffset.VisibilityThreshold
    ),
    targetOffset: (offsetForFullSlide: Int) -> Int = { it }
  ): ExitTransition {
    return when {
      // Note: targetSize could be 0 for empty composables
      towards.isLeft -> slideOutHorizontally(animationSpec) {
        val targetSize = targetSizeMap.targetSize(transition.targetState)
        targetOffset.invoke(-calculateOffset(IntSize(it, it), targetSize).x - it)
      }
      towards.isRight -> slideOutHorizontally(animationSpec) {
        val targetSize = targetSizeMap.targetSize(transition.targetState)
        targetOffset.invoke(
          -calculateOffset(IntSize(it, it), targetSize).x + targetSize.width
        )
      }
      towards == Up -> slideOutVertically(animationSpec) {
        val targetSize = targetSizeMap.targetSize(transition.targetState)
        targetOffset.invoke(-calculateOffset(IntSize(it, it), targetSize).y - it)
      }
      towards == Down -> slideOutVertically(animationSpec) {
        val targetSize = targetSizeMap.targetSize(transition.targetState)
        targetOffset.invoke(
          -calculateOffset(IntSize(it, it), targetSize).y + targetSize.height
        )
      }
      else -> ExitTransition.None
    }
  }

  internal var measuredSize: IntSize by mutableStateOf(IntSize.Zero)
  internal val targetSizeMap = mutableMapOf<T, MutableState<IntSize>>()
  internal var animatedSize: State<IntSize>? = null

  // Current size of the container. If there's any size animation, the current size will be
  // read from the animation value, otherwise we'll use the current
  private val currentSize: IntSize
    get() = animatedSize?.value ?: measuredSize

  @Suppress("ComposableModifierFactory", "ModifierFactoryExtensionFunction")
  @Composable
  internal fun createSizeAnimationModifier(
    contentTransform: ContentTransform
  ): Modifier {
    var shouldAnimateSize by remember(this) { mutableStateOf(false) }
    val sizeTransform = rememberUpdatedState(contentTransform.sizeTransform)
    if (transition.currentState == transition.targetState) {
      shouldAnimateSize = false
    } else {
      if (sizeTransform.value != null) {
        shouldAnimateSize = true
      }
    }
    return if (shouldAnimateSize) {
      val sizeAnimation = transition.createDeferredAnimation(IntSize.VectorConverter)
      remember(sizeAnimation) {
        (if (sizeTransform.value?.clip == false) Modifier else Modifier.clipToBounds())
          .then(SizeModifier(sizeAnimation, sizeTransform))
      }
    } else {
      animatedSize = null
      Modifier
    }
  }

  // This helps track the target measurable without affecting the placement order. Target
  // measurable needs to be measured first but placed last.
  internal data class ChildData(val isTarget: Boolean) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@ChildData
  }

  private inner class SizeModifier constructor(
    val sizeAnimation: Transition<List<T>>.DeferredAnimation<IntSize, AnimationVector2D>,
    val sizeTransform: State<SizeTransform?>,
  ) : LayoutModifier {
    override fun MeasureScope.measure(
      measurable: Measurable,
      constraints: Constraints
    ): MeasureResult {
      val placeable = measurable.measure(constraints)
      val size = sizeAnimation.animate(
        transitionSpec = {
          val initial = targetSizeMap.targetSize(initialState)
          val target = targetSizeMap.targetSize(targetState)
          sizeTransform.value?.createAnimationSpec(initial, target) ?: spring()
        }
      ) { targetSizeMap.targetSize(it) }
      animatedSize = size
      val offset = contentAlignment.align(
        IntSize(placeable.width, placeable.height), size.value, LayoutDirection.Ltr
      )
      return layout(size.value.width, size.value.height) {
        placeable.place(offset)
      }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
      measurable: IntrinsicMeasurable,
      height: Int
    ) = measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
      measurable: IntrinsicMeasurable,
      width: Int
    ) = measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
      measurable: IntrinsicMeasurable,
      height: Int
    ) = measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
      measurable: IntrinsicMeasurable,
      width: Int
    ) = measurable.maxIntrinsicHeight(width)
  }
}

private fun <T> Map<T, State<IntSize>>.targetSize(state: List<T>): IntSize = state.maxByOrNull {
  this[it]?.value?.let { it.width + it.height } ?: 0
}?.let { this[it]?.value } ?: IntSize.Zero

@Composable fun <T> Transition<List<T>>.AnimatedStack(
  modifier: Modifier = Modifier,
  transitionSpec: AnimatedStackScope<T>.() -> ContentTransform = {
    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) with
        fadeOut(animationSpec = tween(90))
  },
  contentAlignment: Alignment = Alignment.TopStart,
  contentKey: (T) -> Any? = { it },
  contentOpaque: (T) -> Boolean = { false },
  content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
  fun List<T>.filterVisible(): List<T> = buildList {
    for (stateForContent in this@filterVisible.reversed()) {
      add(0, stateForContent)
      if (!contentOpaque(stateForContent)) break
    }
  }

  val layoutDirection = LocalLayoutDirection.current
  val rootScope = remember(this) {
    AnimatedStackScope(this, contentAlignment, layoutDirection)
  }

  val currentlyVisible = remember(this) { currentState.filterVisible().toMutableStateList() }
  val contentMap = remember(this) { mutableMapOf<T, @Composable () -> Unit>() }

  if (currentState.filterVisible() == targetState.filterVisible()) {
    rootScope.contentAlignment = contentAlignment
    rootScope.layoutDirection = layoutDirection
  }

  targetState.filterVisible().forEach {
    if (it !in currentlyVisible)
      currentlyVisible.add(it)
  }

  if (currentState.filterVisible().any { it !in contentMap } ||
    targetState.filterVisible().any { it !in contentMap }) {
    contentMap.clear()
    currentlyVisible.forEach { stateForContent ->
      contentMap[stateForContent] = {
        val specOnEnter = remember { transitionSpec(rootScope) }
        // NOTE: enter and exit for this AnimatedVisibility will be using different spec,
        // naturally.
        val exit =
          remember(stateForContent in segment.targetState) {
            if (stateForContent in segment.targetState) {
              ExitTransition.None
            } else {
              rootScope.transitionSpec().initialContentExit
            }
          }

        AnimatedVisibility(
          visible = { stateForContent in it.filterVisible() },
          enter = specOnEnter.targetContentEnter,
          exit = exit,
          modifier = Modifier
            .layout { measurable, constraints ->
              val placeable = measurable.measure(constraints)
              rootScope.targetSizeMap.getOrPut(stateForContent) {
                mutableStateOf(IntSize.Zero)
              }.value = IntSize(placeable.width, placeable.height)
              layout(placeable.width, placeable.height) {
                placeable.place(0, 0, zIndex = specOnEnter.targetContentZIndex)
              }
            }
            .then(AnimatedStackScope.ChildData(stateForContent in targetState.filterVisible()))
        ) {
          DisposableEffect(this) {
            onDispose {
              currentlyVisible.remove(stateForContent)
              rootScope.targetSizeMap.remove(stateForContent)
            }
          }
          content(stateForContent)
        }
      }
    }
  }

  val contentTransform = remember(rootScope, segment) { transitionSpec(rootScope) }
  val sizeModifier = rootScope.createSizeAnimationModifier(contentTransform)
  Layout(
    modifier = modifier.then(sizeModifier),
    content = {
      currentlyVisible.forEach {
        key(contentKey(it)) {
          contentMap[it]?.invoke()
        }
      }
    },
    measurePolicy = remember { AnimatedContentMeasurePolicy(rootScope) }
  )
}

private class AnimatedContentMeasurePolicy(val rootScope: AnimatedStackScope<*>) : MeasurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints
  ): MeasureResult {
    val placeables = arrayOfNulls<Placeable>(measurables.size)
    // Measure the target composable first (but place it on top unless zIndex is specified)
    measurables.forEachIndexed { index, measurable ->
      if ((measurable.parentData as? AnimatedStackScope.ChildData)?.isTarget == true) {
        placeables[index] = measurable.measure(constraints)
      }
    }
    // Measure the non-target composables after target, since these have no impact on
    // container size in the size animation.
    measurables.forEachIndexed { index, measurable ->
      if (placeables[index] == null) {
        placeables[index] = measurable.measure(constraints)
      }
    }

    val maxWidth: Int = placeables.maxByOrNull { it?.width ?: 0 }?.width ?: 0
    val maxHeight = placeables.maxByOrNull { it?.height ?: 0 }?.height ?: 0
    rootScope.measuredSize = IntSize(maxWidth, maxHeight)
    // Position the children.
    return layout(maxWidth, maxHeight) {
      placeables.forEach { placeable ->
        placeable?.let {
          val offset = rootScope.contentAlignment.align(
            IntSize(it.width, it.height),
            IntSize(maxWidth, maxHeight),
            LayoutDirection.Ltr
          )
          it.place(offset.x, offset.y)
        }
      }
    }
  }

  override fun IntrinsicMeasureScope.minIntrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int
  ) = measurables.asSequence().map { it.minIntrinsicWidth(height) }.maxOrNull() ?: 0

  override fun IntrinsicMeasureScope.minIntrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int
  ) = measurables.asSequence().map { it.minIntrinsicHeight(width) }.maxOrNull() ?: 0

  override fun IntrinsicMeasureScope.maxIntrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int
  ) = measurables.asSequence().map { it.maxIntrinsicWidth(height) }.maxOrNull() ?: 0

  override fun IntrinsicMeasureScope.maxIntrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int
  ) = measurables.asSequence().map { it.maxIntrinsicHeight(width) }.maxOrNull() ?: 0
}

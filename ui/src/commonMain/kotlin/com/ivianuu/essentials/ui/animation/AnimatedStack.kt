package com.ivianuu.essentials.ui.animation

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import kotlin.collections.set

@Composable fun AnimatedVisibility(
  visible: Boolean,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<Boolean> = ElementTransitionSpec { materialFade() },
  content: @Composable AnimatedVisibilityScope.() -> Unit
) {
  AnimatedContent(state = visible, transitionSpec = transitionSpec) {
    if (it)
      Box(modifier, propagateMinConstraints = true) { content() }
  }
}

@Composable fun <T> AnimatedContent(
  state: T,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<T> = ElementTransitionSpec { materialFadeThrough() },
  contentAlignment: Alignment = Alignment.TopStart,
  content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
  AnimatedStack(
    items = listOf(element = state),
    modifier = modifier,
    transitionSpec = transitionSpec,
    contentAlignment = contentAlignment,
    content = content
  )
}

@Composable fun <T> AnimatedStack(
  items: List<T>,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<T> = ElementTransitionSpec { materialFadeThrough() },
  contentAlignment: Alignment = Alignment.TopStart,
  contentOpaque: (T) -> Boolean = { false },
  content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
  val transition = updateTransition(targetState = items)

  val scope = remember(transition) {
    AnimatedStackScope(transition, contentAlignment)
  }

  scope.contentAlignment = contentAlignment
  scope.contentOpaque = contentOpaque

  val currentlyVisible = remember(transition) {
    transition.currentState.filterVisible(contentOpaque).toMutableStateList()
  }

  transition.targetState.filterVisible(contentOpaque).fastForEach {
    if (it !in currentlyVisible)
      currentlyVisible.add(it)
  }

  val enterTransitions = remember { mutableStateMapOf<T, MutableMap<Any, EnterTransition>>() }
  val exitTransitions = remember { mutableStateMapOf<T, MutableMap<Any, ExitTransition>>() }
  val containerSizeTransform = remember { mutableStateOf<SizeTransform?>(SizeTransform()) }

  val density = LocalDensity.current
  fun updateTransitions(
    initial: T?,
    target: T?,
    isPush: Boolean,
    isMain: Boolean
  ) {
    if (currentlyVisible.indexOf(initial) != -1 && currentlyVisible.indexOf(target) != -1) {
      if (isPush && currentlyVisible.indexOf(target) < currentlyVisible.indexOf(initial)) {
        currentlyVisible.removeAt(currentlyVisible.indexOf(target))
        currentlyVisible.add(currentlyVisible.indexOf(initial) + 1, target as T)
      } else if (!isPush && currentlyVisible.indexOf(target) > currentlyVisible.indexOf(initial)) {
        currentlyVisible.removeAt(currentlyVisible.indexOf(target))
        currentlyVisible.add(currentlyVisible.indexOf(initial), target as T)
      }
    }

    ElementTransitionBuilder(
      initial,
      target,
      isPush,
      density
    ).apply { with(transitionSpec) { invoke() } }
      .let { builder ->
        if (target != null)
          enterTransitions[target] = builder.enterTransitions
        if (initial != null)
          exitTransitions[initial] = builder.exitTransitions

        if (isMain)
          containerSizeTransform.value = builder.containerSizeTransform
      }
  }

  if (transition.targetState != transition.currentState) {
    remember(transition.targetState, transition.currentState) {
      val initialVisibleItems = transition.currentState.filterVisible(contentOpaque)
      val targetVisibleItems = transition.targetState.filterVisible(contentOpaque)

      if (initialVisibleItems != targetVisibleItems) {
        val initialRootItem = initialVisibleItems.firstOrNull()
        val targetRootItem = targetVisibleItems.firstOrNull()

        // check if we should animate the root items
        val replacingRootItems = targetRootItem != null && initialRootItem != targetRootItem &&
            targetVisibleItems.count { it !in initialVisibleItems } == 1

        val newRootIsPush = targetRootItem !in transition.currentState

        // Replace the old visible root with the new one
        if (replacingRootItems)
          updateTransitions(
            initialRootItem,
            targetRootItem,
            newRootIsPush,
            true
          )

        val itemsToRemove = initialVisibleItems
          .drop(if (replacingRootItems) 1 else 0)
          .reversed()
          .filterNot { it in targetVisibleItems }
        val itemsToAdd = targetVisibleItems
          .drop(if (replacingRootItems) 1 else 0)
          .filterNot { it in initialVisibleItems }

        // Remove all visible items which shouldn't be visible anymore
        // from top to bottom
        itemsToRemove.fastForEachIndexed { i, item ->
          updateTransitions(
            item,
            null,
            newRootIsPush,
            !replacingRootItems && itemsToAdd.isEmpty() && i == itemsToRemove.lastIndex
          )
        }

        // Add any new items to the stack from bottom to top
        itemsToAdd.fastForEachIndexed { i, item ->
          updateTransitions(
            targetVisibleItems.getOrNull(i - 1),
            item,
            true,
            !replacingRootItems && itemsToAdd.lastIndex == i
          )
        }
      }
    }
  }

  Layout(
    modifier = modifier.then(scope.createSizeAnimationModifier(containerSizeTransform)),
    content = {
      currentlyVisible.forEach { itemForContent ->
        key(itemForContent) {
          val enterTransitionsForItem = enterTransitions[itemForContent] ?: emptyMap()
          val exitTransitionsForItem = exitTransitions[itemForContent] ?: emptyMap()

          val childData = remember {
            AnimatedStackScope.ChildData(
              itemForContent in transition.targetState.filterVisible(contentOpaque)
            )
          }

          transition.AnimatedVisibility(
            visible = { itemForContent in it.filterVisible(contentOpaque) },
            enter = enterTransitionsForItem[ContentKey] ?: EnterTransition.None,
            exit = exitTransitionsForItem[ContentKey] ?: ExitTransition.None,
            modifier = childData
              .apply { isTarget = itemForContent in transition.targetState.filterVisible(contentOpaque) }
              .onSizeChanged {
                scope.targetSizeMap.getOrPut(itemForContent) {
                  mutableStateOf(it)
                }.value = it
              }
          ) {
            DisposableEffect(this) {
              onDispose {
                currentlyVisible.remove(itemForContent)
                enterTransitions.remove(itemForContent)
                exitTransitions.remove(itemForContent)
                scope.targetSizeMap.remove(itemForContent)
              }
            }

            CompositionLocalProvider(
              LocalElementAnimationScope provides remember(this, enterTransitionsForItem, exitTransitionsForItem) {
                ElementAnimationScope(
                  this,
                  enterTransitionsForItem,
                  exitTransitionsForItem
                )
              }
            ) {
              content(itemForContent)
            }
          }
        }
      }
    },
    measurePolicy = remember { AnimatedStackMeasurePolicy(scope) }
  )
}

private fun <T> List<T>.filterVisible(contentOpaque: (T) -> Boolean): List<T> = buildList {
  for (i in this@filterVisible.lastIndex downTo 0) {
    val item = this@filterVisible[i]
    add(0, item)
    if (!contentOpaque(item)) break
  }
}

@Stable class AnimatedStackScope<T>(
  private val transition: Transition<List<T>>,
  var contentAlignment: Alignment
) {
  internal var measuredSize: IntSize by mutableStateOf(IntSize.Zero)
  internal val targetSizeMap = mutableMapOf<T, MutableState<IntSize>>()
  internal var animatedSize: State<IntSize>? = null
  var contentOpaque: (T) -> Boolean = { false }

  val currentSize: IntSize
    get() = animatedSize?.value ?: measuredSize

  @SuppressLint("RestrictedApi")
  @Composable fun createSizeAnimationModifier(
    sizeTransform: State<SizeTransform?>
  ): Modifier = if (sizeTransform.value != null) {
    val sizeAnimation = transition.createDeferredAnimation(IntSize.VectorConverter)
    remember(sizeAnimation) {
      (if (sizeTransform.value?.clip == false) Modifier else Modifier.clipToBounds())
        .then(SizeModifier(sizeAnimation, sizeTransform))
    }
  } else {
    animatedSize = null
    Modifier
  }

  internal data class ChildData(var isTarget: Boolean) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@ChildData
  }

  private inner class SizeModifier(
    @SuppressLint("RestrictedApi")
    val sizeAnimation: Transition<List<T>>.DeferredAnimation<IntSize, AnimationVector2D>,
    val sizeTransform: State<SizeTransform?>,
  ) : LayoutModifier {
    @SuppressLint("RestrictedApi")
    override fun MeasureScope.measure(
      measurable: Measurable,
      constraints: Constraints
    ): MeasureResult {
      fun List<T>.maxTargetSize(): IntSize {
        var maxSize = IntSize.Zero
        forEach { item ->
          val itemSize = targetSizeMap[item]?.value ?: IntSize.Zero
          if (itemSize.width + itemSize.height > maxSize.width + maxSize.height)
            maxSize = itemSize
        }

        return maxSize
      }

      val placeable = measurable.measure(constraints)
      val size = sizeAnimation.animate(
        transitionSpec = {
          val initial = initialState.maxTargetSize()
          val target = targetState.maxTargetSize()

          sizeTransform.value?.createAnimationSpec(initial, target) ?: spring()
        }
      ) { it.maxTargetSize() }
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

private class AnimatedStackMeasurePolicy(val scope: AnimatedStackScope<*>) : MeasurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints
  ): MeasureResult {
    val placeables = arrayOfNulls<Placeable>(measurables.size)
    // Measure the target composable first (but place it on top unless zIndex is specified)
    measurables.fastForEachIndexed { index, measurable ->
      if ((measurable.parentData as? AnimatedStackScope.ChildData)?.isTarget == true) {
        placeables[index] = measurable.measure(constraints)
      }
    }
    // Measure the non-target composables after target, since these have no impact on
    // container size in the size animation.
    measurables.fastForEachIndexed { index, measurable ->
      if (placeables[index] == null) {
        placeables[index] = measurable.measure(constraints)
      }
    }

    val maxWidth = placeables.maxByOrNull { it?.width ?: 0 }?.width ?: 0
    val maxHeight = placeables.maxByOrNull { it?.height ?: 0 }?.height ?: 0
    scope.measuredSize = IntSize(maxWidth, maxHeight)
    // Position the children.
    return layout(maxWidth, maxHeight) {
      placeables.forEach { placeable ->
        placeable?.let {
          val offset = scope.contentAlignment.align(
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

@Stable fun interface ElementTransitionSpec<T> {
  operator fun ElementTransitionBuilder<T>.invoke()
}

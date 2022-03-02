/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.*
import kotlinx.coroutines.*
import kotlin.time.Duration

typealias StackTransition = suspend StackTransitionScope.() -> Unit

interface StackTransitionScope : CoroutineScope {
  val state: AnimatedStackState<*>
  val isPush: Boolean
  val from: AnimatedStackChild<*>?
  val to: AnimatedStackChild<*>?
  val fromWillBeRemoved: Boolean
  fun attachTo()
  fun detachFrom()
}

fun ContentAnimationStackTransition(
  spec: AnimationSpec<Float> = defaultAnimationSpec(),
  block: StackTransitionScope.(MutableState<Modifier>?, MutableState<Modifier>?, Float) -> Unit
): StackTransition = {
  val fromModifier = fromElementModifier(ContentAnimationElementKey)
  val toModifier = toElementModifier(ContentAnimationElementKey)
  if (isPush) toModifier?.value = Modifier.alpha(0f)
  attachTo()
  guarantee(
    block = { animate(spec) { block(fromModifier, toModifier, it) } },
    finalizer = { fromModifier?.value = Modifier; toModifier?.value = Modifier }
  )
}

suspend fun MutableState<Modifier>.awaitLayoutCoordinates(): LayoutCoordinates {
  val coordinates = CompletableDeferred<LayoutCoordinates>()
  val previousModifier = value
  value = previousModifier.onGloballyPositioned { coordinates.complete(it) }
  return try {
    coordinates.await()
  } finally {
    value = previousModifier
  }
}

val LayoutCoordinates.rootCoordinates: LayoutCoordinates
  get() = parentCoordinates?.rootCoordinates ?: this

fun StackTransitionScope.overlay(overlay: @Composable () -> Unit): Job = launch(
  start = CoroutineStart.UNDISPATCHED
) {
  state.animationOverlays += overlay
  onCancel { state.animationOverlays -= overlay }
}

suspend fun StackTransitionScope.animate(
  spec: AnimationSpec<Float> = defaultAnimationSpec(),
  block: (Float) -> Unit
) {
  Animatable(0f).animateTo(1f, spec) {
    block(value)
  }
}

fun defaultAnimationSpec(
  duration: Duration = 300.milliseconds,
  delay: Duration = 0.milliseconds,
  easing: Easing = LinearEasing
) = tween<Float>(
  durationMillis = duration.inWholeMilliseconds.toInt(),
  delayMillis = delay.inWholeMilliseconds.toInt(),
  easing = easing
)

fun StackTransitionScope.fromElement(key: Any): AnimationElement? = from?.let { element(it, key) }

fun StackTransitionScope.toElement(key: Any): AnimationElement? = to?.let { element(it, key) }

private var refKeys = 0

fun StackTransitionScope.element(child: AnimatedStackChild<*>, key: Any): AnimationElement {
  val refKey = refKeys++
  val element = child.elementStore.referenceElement(key, refKey)
  launch {
    onCancel {
      child.elementStore.disposeRef(key, refKey)
    }
  }
  return element
}

fun <T> StackTransitionScope.fromElementProp(
  elementKey: Any,
  propKey: AnimationElementPropKey<T>
) = from?.let { elementProp(it, elementKey, propKey) }

fun <T> StackTransitionScope.toElementProp(
  elementKey: Any,
  propKey: AnimationElementPropKey<T>
) = to?.let { elementProp(it, elementKey, propKey) }

fun <T> StackTransitionScope.elementProp(
  child: AnimatedStackChild<*>,
  elementKey: Any,
  propKey: AnimationElementPropKey<T>,
) = element(child, elementKey)[propKey]

fun StackTransitionScope.fromElementModifier(key: Any): MutableState<Modifier>? =
  from?.let { elementModifier(it, key) }

fun StackTransitionScope.toElementModifier(key: Any): MutableState<Modifier>? =
  to?.let { elementModifier(it, key) }

fun StackTransitionScope.elementModifier(
  child: AnimatedStackChild<*>,
  key: Any,
): MutableState<Modifier> {
  val modifier = mutableStateOf<Modifier>(Modifier)
  val element = element(child, key)
  element.modifiers += modifier
  launch {
    onCancel {
      element.modifiers -= modifier
    }
  }
  return modifier
}

operator fun StackTransition.plus(other: StackTransition): StackTransition = {
  par({ this@plus() }, { other() })
}

val NoOpStackTransition: StackTransition = { }

val LocalStackTransition = staticCompositionLocalOf { NoOpStackTransition }

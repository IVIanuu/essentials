/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.coroutines.par
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.essentials.ui.animation.AnimatedStackState
import com.ivianuu.essentials.ui.animation.AnimationElement
import com.ivianuu.essentials.ui.animation.AnimationElementKey
import com.ivianuu.essentials.ui.animation.AnimationElementPropKey
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
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
  block(fromModifier, toModifier, 0f)
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

fun StackTransitionScope.fromElement(key: AnimationElementKey): AnimationElement? =
  from?.let { element(it, key) }

fun StackTransitionScope.toElement(key: AnimationElementKey): AnimationElement? =
  to?.let { element(it, key) }

fun StackTransitionScope.element(
  child: AnimatedStackChild<*>,
  key: AnimationElementKey
): AnimationElement =
  child.elementStore.elementFor(key)

fun <T> StackTransitionScope.fromElementProp(
  elementKey: AnimationElementKey,
  propKey: AnimationElementPropKey<T>
) = from?.let { elementProp(it, elementKey, propKey) }

fun <T> StackTransitionScope.toElementProp(
  elementKey: AnimationElementKey,
  propKey: AnimationElementPropKey<T>
) = to?.let { elementProp(it, elementKey, propKey) }

fun <T> StackTransitionScope.elementProp(
  child: AnimatedStackChild<*>,
  elementKey: AnimationElementKey,
  propKey: AnimationElementPropKey<T>,
) = element(child, elementKey)[propKey]

fun StackTransitionScope.fromElementModifier(key: AnimationElementKey): MutableState<Modifier>? =
  from?.let { elementModifier(it, key) }

fun StackTransitionScope.toElementModifier(key: AnimationElementKey): MutableState<Modifier>? =
  to?.let { elementModifier(it, key) }

fun StackTransitionScope.elementModifier(
  child: AnimatedStackChild<*>,
  key: AnimationElementKey,
): MutableState<Modifier> {
  val modifier = mutableStateOf<Modifier>(Modifier)
  val element = element(child, key)
  element.modifiers += modifier
  coroutineContext.job.invokeOnCompletion { element.modifiers -= modifier }
  return modifier
}

operator fun StackTransition.plus(other: StackTransition): StackTransition = {
  par({ this@plus() }, { other() })
}

val NoOpStackTransition: StackTransition = { }

val LocalStackTransition = staticCompositionLocalOf { NoOpStackTransition }

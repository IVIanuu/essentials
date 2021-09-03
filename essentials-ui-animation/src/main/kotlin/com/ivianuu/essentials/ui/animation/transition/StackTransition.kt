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

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.animation.*
import kotlin.time.*
import kotlinx.coroutines.*

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
  runWithCleanup(
    block = { animate(spec) { block(fromModifier, toModifier, it) } },
    cleanup = { fromModifier?.value = Modifier; toModifier?.value = Modifier }
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
  runOnCancellation { state.animationOverlays -= overlay }
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
) = TweenSpec<Float>(
  durationMillis = duration.toLongMilliseconds().toInt(),
  delay = delay.toLongMilliseconds().toInt(),
  easing = easing
)

fun StackTransitionScope.fromElement(key: Any): AnimationElement? = from?.let { element(it, key) }

fun StackTransitionScope.toElement(key: Any): AnimationElement? = to?.let { element(it, key) }

fun StackTransitionScope.element(
  child: AnimatedStackChild<*>,
  key: Any,
): AnimationElement {
  val refKey = "stack transition $key ${child.key} $this"
  val element = child.elementStore.referenceElement(key, refKey)
  launch {
    runOnCancellation {
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
    runOnCancellation {
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

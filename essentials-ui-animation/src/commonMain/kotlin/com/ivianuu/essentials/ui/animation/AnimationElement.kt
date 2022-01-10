/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.ivianuu.essentials.cast

@Stable class AnimationElement(val key: Any) {
  internal val modifiers = mutableStateListOf<State<Modifier>>()
  internal val refs = mutableSetOf<Any>()
  private val props = mutableStateMapOf<AnimationElementPropKey<*>, Any?>()

  internal operator fun <T> set(key: AnimationElementPropKey<T>, value: T) {
    props[key] = value
  }

  operator fun <T> get(key: AnimationElementPropKey<T>): T? = props[key] as? T
}

object ContentAnimationElementKey

class AnimationElementPropKey<T>

@Stable class AnimationElementStore {
  private val elements = mutableMapOf<Any, AnimationElement>()

  fun referenceElement(elementKey: Any, refKey: Any): AnimationElement {
    val element = elements.getOrPut(elementKey) { AnimationElement(elementKey) }
    element.refs += refKey
    return element
  }

  fun disposeRef(elementKey: Any, refKey: Any) {
    val element = elements[elementKey] ?: return
    element.refs -= refKey
    if (element.refs.isEmpty()) elements -= elementKey
  }
}

@Composable fun rememberAnimationElementFor(key: Any): AnimationElement {
  val stackChild = LocalAnimatedStackChild.current
  val refKey = remember { Any() }
  val element = remember { stackChild.elementStore.referenceElement(key, refKey) }
  DisposableEffect(refKey) {
    onDispose { stackChild.elementStore.disposeRef(key, refKey) }
  }
  return element
}

fun Modifier.animationElement(
  key: Any,
  vararg props: Pair<AnimationElementPropKey<*>, Any?>
): Modifier = composed {
  val element = rememberAnimationElementFor(key)
  props.forEach { element[it.first.cast<AnimationElementPropKey<Any?>>()] = it.second }
  element.modifiers.toSet().fold(Modifier as Modifier) { acc, modifier ->
    acc.then(modifier.value)
  }
}

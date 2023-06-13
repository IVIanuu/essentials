/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlin.collections.set

@Stable class AnimationElement(val key: AnimationElementKey) {
  internal val modifiers = mutableStateListOf<State<Modifier>>()
  private val props = mutableStateMapOf<AnimationElementPropKey<*>, Any?>()

  internal operator fun <T> set(key: AnimationElementPropKey<T>, value: T) {
    props[key] = value
  }

  operator fun <T> get(key: AnimationElementPropKey<T>): T? = props[key] as? T
}

class AnimationElementKey

val ContentAnimationElementKey = AnimationElementKey()

class AnimationElementPropKey<T>

@Stable class AnimationElementStore {
  private val elements = mutableMapOf<AnimationElementKey, AnimationElement>()
  fun elementFor(key: AnimationElementKey) = elements.getOrPut(key) { AnimationElement(key) }
}

@Composable fun rememberAnimationElementFor(key: AnimationElementKey): AnimationElement =
  LocalAnimatedStackChild.current
    .elementStore
    .elementFor(key)

fun Modifier.animationElement(
  key: AnimationElementKey,
  vararg props: Pair<AnimationElementPropKey<*>, Any?>
): Modifier = composed {
  val element = rememberAnimationElementFor(key)
  props.forEach { element[it.first as AnimationElementPropKey<Any?>] = it.second }
  element.modifiers.toSet().fold(Modifier as Modifier) { acc, modifier ->
    acc.then(modifier.value)
  }
}

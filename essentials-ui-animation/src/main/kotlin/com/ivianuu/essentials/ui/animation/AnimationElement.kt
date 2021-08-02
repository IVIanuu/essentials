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

package com.ivianuu.essentials.ui.animation

import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*

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

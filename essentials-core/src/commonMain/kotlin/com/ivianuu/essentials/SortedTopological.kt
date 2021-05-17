/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials

import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

interface TreeDescriptor<in T> {
  fun key(value: T): Any
  fun dependencies(value: T): Set<Any>
  fun dependents(value: T): Set<Any>
}

fun <T> Collection<T>.sortedTopological(@Given descriptor: TreeDescriptor<T>): List<T> {
  if (isEmpty()) return emptyList()
  val sortedItems = mutableListOf<T>()
  var lastItems = emptyList<T>()
  val realDependencies = mutableMapOf<Any, MutableSet<Any>>()
  forEach { item ->
    realDependencies.getOrPut(descriptor.key(item)) { mutableSetOf() }
      .addAll(descriptor.dependencies(item))
    descriptor.dependents(item).forEach { dependent ->
      realDependencies.getOrPut(dependent) { mutableSetOf() } += descriptor.key(item)
    }
  }
  while (true) {
    val unprocessedItems = this@sortedTopological - sortedItems
    if (unprocessedItems.isEmpty()) break
    // todo improve error message
    check(lastItems != unprocessedItems) {
      "Corrupt collection setup $this"
    }
    lastItems = unprocessedItems
    sortedItems += unprocessedItems
      .filter { item ->
        realDependencies.getValue(descriptor.key(item)).all { dependency ->
          sortedItems.any { descriptor.key(it) == dependency }
        }
      }
  }
  return sortedItems
}

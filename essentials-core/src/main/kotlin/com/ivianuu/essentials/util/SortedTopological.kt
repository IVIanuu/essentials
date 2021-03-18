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

package com.ivianuu.essentials.util

fun <T, K> Collection<T>.sortedTopological(
    key: (T) -> K,
    dependents: (T) -> Set<K>,
    dependencies: (T) -> Set<K>
): List<T> {
    if (isEmpty()) return emptyList()
    val sortedItems = mutableListOf<T>()
    var lastItems = emptyList<T>()
    val realDependencies = mutableMapOf<K, MutableSet<K>>()
    forEach { item ->
        realDependencies.getOrPut(key(item)) { mutableSetOf() } += dependencies(item)
        dependents(item).forEach { dependent ->
            realDependencies.getOrPut(dependent) { mutableSetOf() } += key(item)
        }
    }
    while (true) {
        val unprocessedItems = this - sortedItems
        if (unprocessedItems.isEmpty()) break
        // todo improve error message
        check(lastItems != unprocessedItems) {
            "Corrupt collection setup $this"
        }
        lastItems = unprocessedItems
        sortedItems += unprocessedItems
            .filter { item ->
                realDependencies.getValue(key(item)).all { dependency ->
                    sortedItems.any {
                        key(it) == dependency
                    }
                }
            }
    }
    return sortedItems
}

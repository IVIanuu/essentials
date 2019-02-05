/*
 * Copyright 2018 Manuel Wrage
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



package com.ivianuu.essentials.util.ext

import java.util.*

fun <T> MutableList<T>.swap(from: Int, to: Int) {
    Collections.swap(this, from, to)
}

inline fun <T> Iterable<T>.takeUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in this) {
        list.add(item)
        if (!predicate(item))
            break
    }

    return list
}

inline fun <T> Array<out T>.takeUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in this) {
        list.add(item)
        if (!predicate(item))
            break
    }
    return list
}

inline fun <T> List<T>.takeLastUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in reversed()) {
        list.add(item)
        if (!predicate(item))
            break
    }
    return list
}

inline fun <T> Array<out T>.takeLastUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    for (item in reversed()) {
        list.add(item)
        if (!predicate(item))
            break
    }

    return list
}

inline fun <T, R> Iterable<T>.firstNotNullResult(transform: (T) -> R?): R? {
    for (element in this) {
        val result = transform(element)
        if (result != null) return result
    }
    return null
}

inline fun <T, R> Array<T>.firstNotNullResult(transform: (T) -> R?): R? {
    for (element in this) {
        val result = transform(element)
        if (result != null) return result
    }
    return null
}
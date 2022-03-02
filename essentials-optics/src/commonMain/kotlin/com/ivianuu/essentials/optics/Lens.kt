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

package com.ivianuu.essentials.optics

interface Lens<T, V> {
  fun get(t: T): V
  fun set(t: T, v: V): T
}

inline fun <T, V> Lens(
  crossinline get: (T) -> V,
  crossinline set: (T, V) -> T
): Lens<T, V> = object : Lens<T, V> {
  override fun get(t: T): V = get.invoke(t)
  override fun set(t: T, v: V): T = set.invoke(t, v)
}

inline fun <T, V> Lens<T, V>.update(t: T, transform: V.() -> V): T = set(t, transform(get(t)))

operator fun <A, B, C> Lens<A, B>.plus(other: Lens<B, C>): Lens<A, C> = Lens(
  { a -> other.get(get(a)) },
  { b, c -> set(b, other.set(get(b), c)) }
)

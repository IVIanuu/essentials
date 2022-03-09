/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

interface Elements<N> {
  operator fun <T> invoke(key: TypeKey<T>): T
}

inline operator fun <reified T> Elements<*>.invoke(): T = this(typeKeyOf())

class ElementsImpl<N>(
  private val key: TypeKey<Elements<N>>,
  elements: List<Element<N>>
) : Elements<N> {
  @OptIn(ExperimentalStdlibApi::class)
  private val elements = buildMap {
    for (element in elements)
      this[element.key] = element.value
  }

  override fun <T> invoke(key: TypeKey<T>): T =
    elements[key] as T
      ?: error("No element found for $key in ${this.key}")
}

class Element<N>(val value: Any, val key: TypeKey<*>)

inline fun <reified N> ProviderRegistry.elements() {
  provide<Elements<N>> { ElementsImpl(typeKeyOf(), get()) }
}

inline fun <reified N, reified T : Any> ProviderRegistry.element(
  scopeName: N,
  noinline factory: ProviderScope.() -> T
) {
  provide { Element<N>(factory(), typeKeyOf<T>()) }
}

inline fun <reified N, reified T : Any> ProviderRegistry.eagerInit(scopeName: N) {
  element(scopeName) { EagerInit<T>(get<T>()) }
}

@PublishedApi @JvmInline internal value class EagerInit<T>(val value: Any)

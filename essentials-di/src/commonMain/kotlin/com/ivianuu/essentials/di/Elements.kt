/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

interface Elements<N> {
  operator fun <T> invoke(key: TypeKey<T>): T
}

inline operator fun <reified T> Elements<*>.invoke(): T = this(typeKeyOf())

val ElementsModule = module {
  addClassifierProvider<Elements<Any?>> { key ->
    val nameKey = key.arguments[0]
    val elementKey = typeKeyOf<Element<Any?>>(arrayOf(nameKey))
    val listKey = typeKeyOf<List<Element<Any?>>>(arrayOf(elementKey))
    val elements = get(listKey)
    ElementsImpl(key, elements)
  }
}

class ElementsImpl<N>(
  private val key: TypeKey<Elements<N>>,
  elements: List<Element<N>>
) : Elements<N> {
  @OptIn(ExperimentalStdlibApi::class)
  private val elements = buildMap<TypeKey<*>, Any> {
    for (element in elements)
      this[element.key] = element.value
  }

  override fun <T> invoke(key: TypeKey<T>): T =
    elements[key] as T
      ?: error("No element found for $key in ${this.key}")
}

class Element<N>(val value: Any, val key: TypeKey<Any>) {
  companion object {
    inline operator fun <N, reified T : Any> invoke(value: T) = Element<N>(value, typeKeyOf())
    inline operator fun <N, reified T : Any> invoke(scopeName: N, value: T) = Element<N>(value, typeKeyOf())
  }
}

/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.common.TypeKey

sealed class LoadingOrder<T> {
  sealed class Static<T> : LoadingOrder<T>() {
    class First<T> : Static<T>()

    class Last<T> : Static<T>()

    class None<T> : LoadingOrder<T>() {
      fun first() = First<T>()

      fun last() = Last<T>()

      fun <S> before(@Inject other: TypeKey<S>): Topological.Before<T> =
        Topological.Before(other.cast())

      fun <S> after(@Inject other: TypeKey<S>): Topological.After<T> = Topological.After(other.cast())
    }
  }

  sealed class Topological<T> : LoadingOrder<T>() {
    data class Before<T>(val key: TypeKey<T>) : Topological<T>()

    data class After<T>(val key: TypeKey<T>) : Topological<T>()

    data class Combined<T>(val a: Topological<T>, val b: Topological<T>) : Topological<T>()

    operator fun plus(other: Topological<T>): Combined<T> = Combined(this, other)

    fun <S> before(@Inject other: TypeKey<S>): Combined<T> = Combined(this, Before(other).cast())

    fun <S> after(@Inject other: TypeKey<S>): Combined<T> = Combined(this, After(other).cast())
  }

  interface Descriptor<in T> {
    fun key(item: T): TypeKey<*>

    fun loadingOrder(item: T): LoadingOrder<*>
  }

  companion object {
    operator fun <T> invoke() = Static.None<T>()
  }
}

private fun LoadingOrder.Topological<*>.dependencies(): Set<TypeKey<*>> {
  val result = mutableSetOf<TypeKey<*>>()

  fun LoadingOrder.Topological<*>.collect() {
    when (this) {
      is LoadingOrder.Topological.After -> result += key
      is LoadingOrder.Topological.Combined -> {
        a.collect()
        b.collect()
      }
      else -> {}
    }
  }

  collect()

  return result
}

private fun LoadingOrder.Topological<*>.dependents(): Set<TypeKey<*>> {
  val result = mutableSetOf<TypeKey<*>>()

  fun LoadingOrder.Topological<*>.collect() {
    when (this) {
      is LoadingOrder.Topological.Before -> result += key
      is LoadingOrder.Topological.Combined -> {
        a.collect()
        b.collect()
      }
      else -> {}
    }
  }

  collect()

  return result
}

fun <T> Collection<T>.sortedWithLoadingOrder(@Inject descriptor: LoadingOrder.Descriptor<T>): List<T> {
  if (isEmpty() || size == 1) return toList()

  val first = mapNotNull {
    if (descriptor.loadingOrder(it) is LoadingOrder.Static.First)
      descriptor.key(it)
    else null
  }

  val dependencies = mutableMapOf<TypeKey<*>, MutableSet<TypeKey<*>>>()

  // collect dependencies for each item
  for (item in this) {
    val key = descriptor.key(item)

    if (key in first) continue

    val loadingOrder = descriptor.loadingOrder(item)

    val itemDependencies = dependencies.getOrPut(key) { mutableSetOf() }

    when (loadingOrder) {
      is LoadingOrder.Static.First -> throw AssertionError()
      is LoadingOrder.Static.Last -> {
        itemDependencies += filter { other ->
          when (val otherLoadingOrder = descriptor.loadingOrder(other)) {
            is LoadingOrder.Static.Last -> false
            is LoadingOrder.Topological -> otherLoadingOrder.dependencies()
              .none { it == key }
            else -> true
          }
        }.map { descriptor.key(it) }
      }
      is LoadingOrder.Topological -> {
        itemDependencies += loadingOrder.dependencies()
        loadingOrder.dependents().forEach { dependentKey ->
          dependencies.getOrPut(dependentKey) { mutableSetOf() } += key
        }
      }
      else -> {}
    }

    itemDependencies += first
  }

  val sortedItems = mutableListOf<T>()
  var lastItems = emptyList<T>()

  // write final list
  while (true) {
    val unprocessedItems = this - sortedItems
    if (unprocessedItems.isEmpty()) break
    // todo improve error message
    check(lastItems != unprocessedItems) {
      "Corrupt collection setup last and unprocessed$unprocessedItems\nall $this"
    }
    lastItems = unprocessedItems
    sortedItems += unprocessedItems
      .filter { item ->
        dependencies[descriptor.key(item)]?.all { dependency ->
          sortedItems.any { descriptor.key(it) == dependency }
        } ?: true
      }
  }

  return sortedItems
}

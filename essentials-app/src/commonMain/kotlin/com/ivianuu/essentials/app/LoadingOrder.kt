/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials.app

import com.ivianuu.essentials.di.*

sealed interface LoadingOrder<T> {
  sealed interface Static<T> : LoadingOrder<T> {
    class First<T> : Static<T>

    class Last<T> : Static<T>

    class None<T> : LoadingOrder<T> {
      fun first() = First<T>()

      fun last() = Last<T>()

      inline fun <reified S> before(): Topological.Before<T> =
        Topological.Before(typeKeyOf<S>() as TypeKey<T>)

      inline fun <reified S> after(): Topological.After<T> = Topological.After(typeKeyOf<S>() as TypeKey<T>)
    }
  }

  sealed class Topological<T> : LoadingOrder<T> {
    data class Before<T>(val key: TypeKey<T>) : Topological<T>()

    data class After<T>(val key: TypeKey<T>) : Topological<T>()

    data class Combined<T>(val a: Topological<T>, val b: Topological<T>) : Topological<T>()

    operator fun plus(other: Topological<T>): Combined<T> = Combined(this, other)

    inline fun <reified S> before(): Combined<T> = Combined(this, Before(typeKeyOf<S>()) as Topological<T>)

    inline fun <reified S> after(): Combined<T> = Combined(this, After(typeKeyOf<S>()) as Topological<T>)
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

fun <T> Collection<T>.sortedWithLoadingOrder(
  key: (T) -> TypeKey<*>,
  loadingOrder: (T) -> LoadingOrder<*>
): List<T> {
  if (isEmpty() || size == 1) return toList()

  val first = mapNotNull {
    if (loadingOrder(it) is LoadingOrder.Static.First) key(it)
    else null
  }

  val dependencies = mutableMapOf<TypeKey<*>, MutableSet<TypeKey<*>>>()

  // collect dependencies for each item
  for (item in this) {
    val itemKey = key(item)

    if (itemKey in first) continue

    val itemLoadingOrder = loadingOrder(item)

    val itemDependencies = dependencies.getOrPut(itemKey) { mutableSetOf() }

    when (itemLoadingOrder) {
      is LoadingOrder.Static.First -> throw AssertionError()
      is LoadingOrder.Static.Last -> {
        itemDependencies += filter { other ->
          when (val otherLoadingOrder = loadingOrder(other)) {
            is LoadingOrder.Static.Last -> false
            is LoadingOrder.Topological -> otherLoadingOrder.dependencies()
              .none { it == itemKey }
            else -> true
          }
        }.map { key(it) }
      }
      is LoadingOrder.Topological -> {
        itemDependencies += itemLoadingOrder.dependencies()
        itemLoadingOrder.dependents().forEach { dependentKey ->
          dependencies.getOrPut(dependentKey) { mutableSetOf() } += itemKey
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
        dependencies[key(item)]?.all { dependency ->
          sortedItems.any { key(it) == dependency }
        } ?: true
      }
  }

  return sortedItems
}

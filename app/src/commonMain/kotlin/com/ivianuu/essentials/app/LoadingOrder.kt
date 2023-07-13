/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.common.TypeKey

sealed interface LoadingOrder<T> {
  sealed interface Static<T> : LoadingOrder<T> {
    class First<T> : Static<T>

    class Last<T> : Static<T>

    class None<T> : LoadingOrder<T> {
      fun first() = First<T>()

      fun last() = Last<T>()

      context(TypeKey<S>) fun <S> before(): Topological.Before<T> =
        Topological.Before(this@TypeKey).cast()

      context(TypeKey<S>) fun <S> after(): Topological.After<T> =
        Topological.After(this@TypeKey).cast()
    }
  }

  sealed interface Topological<T> : LoadingOrder<T> {
    data class Before<T>(val key: TypeKey<T>) : Topological<T>

    data class After<T>(val key: TypeKey<T>) : Topological<T>

    data class Combined<T>(val a: Topological<T>, val b: Topological<T>) : Topological<T>

    operator fun plus(other: Topological<T>) = Combined(this, other)

    context(TypeKey<S>) fun <S> before() = Combined(this, Before(this@TypeKey).cast())

    context(TypeKey<S>) fun <S> after() = Combined(this, After(this@TypeKey).cast())
  }

  interface Descriptor<in T> {
    val T.key: TypeKey<*>

    val T.loadingOrder: LoadingOrder<*>
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

context(LoadingOrder.Descriptor<T>) fun <T> Collection<T>.sortedWithLoadingOrder(): List<T> {
  if (isEmpty() || size == 1) return toList()

  val first = mapNotNull {
    if (it.loadingOrder is LoadingOrder.Static.First) it.key
    else null
  }

  val dependencies = mutableMapOf<TypeKey<*>, MutableSet<TypeKey<*>>>()

  // collect dependencies for each item
  for (item in this) {
    if (item.key in first) continue

    val itemDependencies = dependencies.getOrPut(item.key) { mutableSetOf() }

    when (val loadingOrder = item.loadingOrder) {
      is LoadingOrder.Static.First -> throw AssertionError()
      is LoadingOrder.Static.Last -> {
        itemDependencies += filter { other ->
          when (val otherLoadingOrder = other.loadingOrder) {
            is LoadingOrder.Static.Last -> false
            is LoadingOrder.Topological -> otherLoadingOrder.dependencies()
              .none { it == item.key }

            else -> true
          }
        }.map { it.key }
      }
      is LoadingOrder.Topological -> {
        itemDependencies += loadingOrder.dependencies()
        loadingOrder.dependents().forEach { dependentKey ->
          dependencies.getOrPut(dependentKey) { mutableSetOf() } += item.key
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
        dependencies[item.key]?.all { dependency ->
          sortedItems.any { it.key == dependency }
        } ?: true
      }
  }

  return sortedItems
}

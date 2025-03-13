/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import androidx.compose.ui.util.*
import injekt.*
import kotlin.reflect.*

sealed interface LoadingOrder<T : Any> {
  sealed interface Static<T : Any> : LoadingOrder<T> {
    class First<T : Any> : Static<T>

    class Last<T : Any> : Static<T>

    class None<T : Any> : LoadingOrder<T> {
      fun first() = First<T>()

      fun last() = Last<T>()

      fun <S : Any> before(key: KClass<S> = inject): Topological.Before<T> =
        Topological.Before(key).cast()

      fun <S : Any> after(key: KClass<S> = inject): Topological.After<T> =
        Topological.After(key).cast()
    }
  }

  sealed interface Topological<T : Any> : LoadingOrder<T> {
    data class Before<T : Any>(val key: KClass<T>) : Topological<T>

    data class After<T : Any>(val key: KClass<T>) : Topological<T>

    data class Combined<T : Any>(val a: Topological<T>, val b: Topological<T>) : Topological<T>

    operator fun plus(other: Topological<T>) = Combined(this, other)

    fun <S : Any> before(key: KClass<S> = inject) = Combined(this, Before(key).cast())

    fun <S : Any> after(key: KClass<S> = inject) = Combined(this, After(key).cast())
  }

  interface Descriptor<in T> {
    fun key(x: T): KClass<*>

    fun loadingOrder(x: T): LoadingOrder<*>
  }

  companion object {
    operator fun <T : Any> invoke() = Static.None<T>()
  }
}

private fun LoadingOrder.Topological<*>.dependencies(): Set<KClass<*>> {
  val result = mutableSetOf<KClass<*>>()

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

private fun LoadingOrder.Topological<*>.dependents(): Set<KClass<*>> {
  val result = mutableSetOf<KClass<*>>()

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

fun <T> List<T>.sortedWithLoadingOrder(
  descriptor: LoadingOrder.Descriptor<T> = inject
): List<T> {
  if (isEmpty() || size == 1) return toList()

  val first = fastMapNotNull {
    if (descriptor.loadingOrder(it) is LoadingOrder.Static.First)
      descriptor.key(it)
    else null
  }

  val dependencies = mutableMapOf<KClass<*>, MutableSet<KClass<*>>>()

  // collect dependencies for each item
  fastForEach { item ->
    val key = descriptor.key(item)

    if (key in first) return@fastForEach

    val loadingOrder = descriptor.loadingOrder(item)

    val itemDependencies = dependencies.getOrPut(key) { mutableSetOf() }

    when (loadingOrder) {
      is LoadingOrder.Static.First -> throw AssertionError()
      is LoadingOrder.Static.Last -> {
        itemDependencies += fastFilter { other ->
          when (val otherLoadingOrder = descriptor.loadingOrder(other)) {
            is LoadingOrder.Static.Last -> false
            is LoadingOrder.Topological -> otherLoadingOrder.dependencies()
              .none { it == key }

            else -> true
          }
        }.fastMap { descriptor.key(it) }
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
      .fastFilter { item ->
        dependencies[descriptor.key(item)]?.all { dependency ->
          sortedItems.fastAny { descriptor.key(it) == dependency }
        } != false
      }
  }

  return sortedItems
}

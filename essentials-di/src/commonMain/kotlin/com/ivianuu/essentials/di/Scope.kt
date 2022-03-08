/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.di

import kotlinx.atomicfu.locks.*

interface Scope<N> : Disposable {
  operator fun <T : Any> invoke(key: TypeKey<T>, init: () -> T): T
}

fun <N> Scope(): Scope<N> = ScopeImpl()

inline operator fun <reified T : Any> Scope<*>.invoke(noinline init: () -> T) =
  this(typeKeyOf(), init)

private class ScopeImpl<N> : SynchronizedObject(), Scope<N>, Disposable {
  private val values = mutableMapOf<TypeKey<*>, Any>()

  override fun <T : Any> invoke(key: TypeKey<T>, init: () -> T): T =
    synchronized(this) { values.getOrPut(key, init) as T }

  override fun dispose() {
    synchronized(this) {
      values.values.toList()
        .also { values.clear() }
    }.forEach {
      (it as? Disposable)?.dispose()
    }
  }
}

inline fun <reified N, reified T : Any> ProviderScope.scoped(
  scopeName: N,
  crossinline factory: ProviderScope.() -> T
): T {
  val scope = get<Scope<N>>()
  return scope { factory() }
}

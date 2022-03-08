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

inline fun <reified N, reified T : Any> ContainerBuilder.addScoped(
  scopeName: N,
  noinline factory: Container.() -> T
) = addScoped<N, T>(factory)

inline fun <reified N, reified T : Any> ContainerBuilder.addScoped(
  noinline factory: Container.() -> T
) = addScoped(typeKeyOf(), typeKeyOf<N>(), factory)

fun <N, T : Any> ContainerBuilder.addScoped(
  key: TypeKey<T>,
  scopeKey: TypeKey<N>,
  factory: Container.() -> T
) = add(key) {
  val scope = get(
    typeKeyOf<Scope<N>>(
      classifierFqName = Scope::class.qualifiedName!!,
      arguments = arrayOf(scopeKey)
    )
  )

  scope(key) { factory() }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

interface Scope<N> : Disposable {
  val name: TypeKey<N>

  val parent: Scope<*>?

  val isDisposed: Boolean

  fun addObserver(observer: ScopeObserver): Disposable

  fun <T> scoped(key: Any, compute: () -> T): T

  context(TypeKey<T>) fun <T> scoped(compute: () -> T): T = scoped(value, compute)

  context(TypeKey<T>) fun <T : Any> serviceOrNull(): T?

  context(TypeKey<T>) fun <T : Any> service(): T =
    serviceOrNull() ?: error("No service found for $value in ${name.value}")
}

@Provide class ScopeImpl<N>(
  override val name: TypeKey<N>,
  override val parent: @ParentScope Scope<*>? = null,
  services: (Scope<N>, @ParentScope Scope<*>?) -> List<ProvidedService<N, *>>
) : Scope<N>, SynchronizedObject() {
  @PublishedApi internal var _isDisposed = false
  override val isDisposed: Boolean
    get() = synchronized(this) { _isDisposed }

  private val cache = hashMapOf<Any, Any?>()
  private val observers = mutableListOf<ScopeObserver>()

  private val services = buildMap {
    for (service in services(this@ScopeImpl, this@ScopeImpl))
      this[service.key.value] = service
  }

  init {
    this.services.forEach {
      if (it.value is ScopeObserver)
        addObserver(it.value.cast())
    }
  }

  override fun addObserver(observer: ScopeObserver): Disposable {
    synchronized(this) {
      checkDisposed()
      observers += observer
    }

    observer.onEnter(this)

    return Disposable {
      synchronized(this) {
        observer.onExit(this)
        observers -= observer
      }
    }
  }

  context(TypeKey<T>) override fun <T : Any> serviceOrNull(): T? =
    services[value]?.let { return it.get(this) as T }
      ?: parent?.serviceOrNull()

  override fun <T> scoped(key: Any, compute: () -> T): T = synchronized(this) {
    checkDisposed()
    val value = cache.getOrPut(key) { compute() ?: NULL }
    if (value is ScopeObserver)
      addObserver(value)
    (if (value !== NULL) value else null) as T
  }

  override fun dispose() {
    synchronized(this) {
      if (!_isDisposed) {
        _isDisposed = true
        observers.toList().forEach { it.onExit(this) }
        cache.values.toList().forEach {
          if (it is ScopeObserver)
            it.onExit(this)
        }
      }
    }
  }

  private fun checkDisposed() {
    check(!_isDisposed) { "Cannot use a disposed scope" }
  }

  companion object {
    @PublishedApi internal val NULL = Any()
  }
}

interface ProvidedService<N, T> {
  val key: TypeKey<T>

  fun get(scope: Scope<N>): T

  @Provide companion object {
    @Provide fun <N> defaultServices() = emptyList<ProvidedService<N, *>>()

    context(TypeKey<T>) inline operator fun <N, T> invoke(
      crossinline factory: () -> T
    ): ProvidedService<N, T> = object : ProvidedService<N, T> {
      override val key: TypeKey<T>
        get() = this@TypeKey

      override fun get(scope: Scope<N>): T = factory()
    }
  }
}

interface ScopeObserver {
  fun onEnter(scope: Scope<*>) {
  }

  fun onExit(scope: Scope<*>) {
  }
}

@Tag annotation class Scoped<N> {
  @Provide companion object {
    @Provide inline fun <@Spread T : @Scoped<N> S, S : Any, N> scoped(
      crossinline init: () -> T,
      scope: Scope<N>,
      key: TypeKey<S>
    ): S = scope.scoped(key) { init() }
  }
}

@Tag annotation class Service<N> {
  @Provide companion object {
    context(TypeKey<S>) @Provide inline fun <@Spread T : @Service<N> S, S : Any, N> service(
      crossinline factory: () -> T
    ) = ProvidedService<N, S>(factory = factory)

    @Provide inline fun <@Spread T : @Service<N> S, S : Any, N> accessor(service: T): S = service
  }
}

@Tag annotation class ParentScope

@Tag annotation class Eager<N> {
  @Provide companion object {
    @Provide fun <@Spread T : @Eager<N> S, S : Any, N> scoped(value: T): @Scoped<N> S = value

    @Provide inline fun <@Spread T : @Eager<N> S, S : Any, N> service(
      key: TypeKey<S>,
      crossinline factory: () -> S
    ): ProvidedService<N, S> = object : ProvidedService<N, @Initializer S>, ScopeObserver {
      override val key: TypeKey<S>
        get() = key

      private var _value: Any? = null

      override fun get(scope: Scope<N>): S = _value as S

      override fun onEnter(scope: Scope<*>) {
        _value = factory()
      }
    }

    @Tag private annotation class Initializer
  }
}

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import androidx.compose.runtime.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.atomicfu.locks.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide class Scope<N>(
  val name: TypeKey<N>,
  val parent: @ParentScope Scope<*>? = null,
  observers: (Scope<N>, @ParentScope Scope<*>?) -> List<ExtensionPointRecord<ScopeObserver<N>>>,
  services: (Scope<N>, @ParentScope Scope<*>?) -> List<ProvidedService<N, *>>
) : SynchronizedObject() {
  @PublishedApi internal var _isDisposed = false
  val isDisposed: Boolean
    get() = synchronized(this) { _isDisposed }

  @PublishedApi internal val cache = hashMapOf<Any, Any?>()
  private val observers = mutableListOf<ScopeObserver<*>>()

  private val services = buildMap {
    for (service in services(this@Scope, this@Scope))
      this[service.key.value] = service
  }

  private val _children = MutableStateFlow<Set<Scope<*>>>(emptySet())
  val children: StateFlow<Set<Scope<*>>> by this::_children

  init {
    observers(this@Scope, this@Scope)
      .sortedWithLoadingOrder()
      .forEach { addObserver(it.instance) }
  }

  private val parentDisposable = parent?.registerChild(this)

  fun addObserver(observer: ScopeObserver<*>): Disposable {
    if (isDisposed) return Disposable {}

    synchronized(observers) {
      if (observer in observers) return Disposable {}
      observers += observer
    }

    observer.cast<ScopeObserver<N>>().onEnter(this)

    return Disposable {
      observer.cast<ScopeObserver<N>>().onExit(this)
      synchronized(observers) { observers -= observer }
    }
  }

  fun <T : Any> serviceOrNull(@Inject key: TypeKey<T>): T? = services[key.value]?.let {
    return it.factory().unsafeCast()
  } ?: parent?.serviceOrNull(key)

  fun <T : Any> service(@Inject key: TypeKey<T>): T =
    serviceOrNull(key) ?: error("No service found for ${key.value} in ${name.value}")

  inline fun <T> scoped(key: Any, compute: () -> T): T = synchronized(cache) {
    checkDisposed()
    val value = cache.getOrPut(key) {
      compute()
        .also {
          if (it is ScopeObserver<*>)
            addObserver(it)
        }
        ?: NULL
    }
    (if (value !== NULL) value else null).unsafeCast()
  }

  inline fun <T> scoped(@Inject key: TypeKey<T>, compute: () -> T): T =
    scoped(key.value, compute)

  fun dispose() {
    synchronized(this) {
      if (!_isDisposed) {
        _children.value.forEach { it.dispose() }

        _isDisposed = true

        parentDisposable?.dispose()

        synchronized(observers) {
          observers.toList()
            .also { observers.clear() }
        }.forEach {
          it.cast<ScopeObserver<N>>().onExit(this)
        }

        synchronized(cache) {
          cache.values.toList()
            .also { cache.clear() }
        }.forEach { cachedValue ->
          if (cachedValue is Disposable)
            cachedValue.dispose()
        }
      }
    }
  }

  private fun registerChild(child: Scope<*>): Disposable {
    synchronized(_children) { _children.value = _children.value + child }
    return Disposable {
      synchronized(_children) { _children.value = _children.value - child }
    }
  }

  @PublishedApi internal fun checkDisposed() {
    check(!_isDisposed) { "Cannot use a disposed scope" }
  }

  @Provide companion object {
    @PublishedApi internal val NULL = Any()
  }
}

val Scope<*>.root: Scope<*> get() = parent?.root ?: this

val Scope<*>.coroutineScope: CoroutineScope get() = service()

data class ProvidedService<N, T>(val key: TypeKey<T>, val factory: () -> T) {
  @Provide companion object {
    @Provide fun <N> defaultServices() = emptyList<ProvidedService<N, *>>()
  }
}

interface ScopeObserver<N> : ExtensionPoint<ScopeObserver<N>> {
  fun onEnter(scope: Scope<N>) {
  }

  fun onExit(scope: Scope<N>) {
  }

  @Provide companion object {
    @Provide fun <N> defaultObservers() = emptyList<ScopeObserver<N>>()
  }
}

@Tag annotation class Scoped<N> {
  @Provide companion object {
    @Provide inline fun <@Spread T : @Scoped<N> S, S : Any, N> scoped(
      key: TypeKey<S>,
      scope: Scope<N>,
      crossinline init: () -> T,
    ): S = scope.scoped(key) { init() }
  }
}

@Tag annotation class Service<N> {
  @Provide companion object {
    @Provide fun <@Spread T : @Service<N> S, S : Any, N> service(
      key: TypeKey<S>,
      factory: () -> T
    ) = ProvidedService<N, S>(key, factory)

    @Provide fun <@Spread T : @Service<N> S, S : Any, N> accessor(service: T): S = service
  }
}

@Tag annotation class ParentScope

@Tag annotation class Eager<N> {
  @Provide companion object {
    @Provide fun <@Spread T : @Eager<N> S, S : Any, N> scoped(value: T): @Scoped<N> S = value

    @Provide inline fun <@Spread T : @Eager<N> S, S : Any, N> observer(
      key: TypeKey<S>,
      crossinline factory: () -> S
    ): ScopeObserver<N> = object : ScopeObserver<N> {
      override fun onEnter(scope: Scope<N>) {
        factory()
      }
    }
  }
}

val LocalScope = compositionLocalOf<Scope<*>> { error("No provided scope") }

val scopedCoroutineScope: CoroutineScope
  @Composable get() = LocalScope.current.coroutineScope

/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastForEach
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.atomicfu.locks.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Stable @Provide class Scope<N : Any>(
  val name: KClass<N>,
  val parent: @ParentScope Scope<*>? = null,
  observers: (Scope<N>, @ParentScope Scope<*>?) -> List<ExtensionPointRecord<ScopeObserver<N>>>,
  services: (Scope<N>, @ParentScope Scope<*>?) -> List<ProvidedService<N, *>>
) : SynchronizedObject() {
  @PublishedApi internal var _isDisposed = false
  val isDisposed: Boolean
    get() = synchronized(this) { _isDisposed }

  @PublishedApi internal val cache = hashMapOf<Any, Any?>()
  private val observers = mutableListOf<ScopeObserver<*>>()

  private val services: Map<KClass<*>, ProvidedService<*, *>>? = run {
    val providedServices = services(this@Scope, this@Scope)
    if (providedServices.isEmpty()) null
    else buildMap {
      for (providedService in providedServices)
        this[providedService.key] = providedService
    }
  }

  var children by mutableStateOf(emptySet<Scope<*>>())
    private set

  init {
    parent?.registerChild(this)
    observers(this@Scope, this@Scope)
      .sortedWithLoadingOrder()
      .fastForEach { addObserver(it.instance) }
  }

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

  fun <T : Any> serviceOrNull(key: KClass<T> = inject): T? = services?.get(key)?.let {
    return it.factory().unsafeCast()
  } ?: parent?.serviceOrNull(key)

  fun <T : Any> service(key: KClass<T> = inject): T =
    serviceOrNull(key) ?: error("No service found for ${key.qualifiedName} in ${name.qualifiedName}")

  inline fun <T> scoped(key: Any, compute: () -> T): T {
    cache[key]?.let { if (it !== NULL) return it.unsafeCast() }
    return synchronized(cache) {
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
  }

  inline fun <T : Any> scoped(key: TypeKey<T> = inject, compute: () -> T): T =
    scoped(key.value, compute)

  fun dispose() {
    synchronized(this) {
      if (!_isDisposed) {
        children.forEach { it.dispose() }

        _isDisposed = true

        parent?.unregisterChild(this)

        synchronized(observers) {
          observers.toList()
            .also { observers.clear() }
        }.fastForEach {
          it.cast<ScopeObserver<N>>().onExit(this)
        }

        synchronized(cache) {
          cache.values.toList()
            .also { cache.clear() }
        }.fastForEach { cachedValue ->
          if (cachedValue is Disposable)
            cachedValue.dispose()
        }
      }
    }
  }

  private fun registerChild(child: Scope<*>) {
    children += child
  }

  private fun unregisterChild(child: Scope<*>) {
    children -= child
  }

  @PublishedApi internal fun checkDisposed() {
    check(!_isDisposed) { "Cannot use a disposed scope" }
  }

  @Provide companion object {
    @PublishedApi internal val NULL = Any()
  }
}

val Scope<*>.allScopes: Set<Scope<*>> get() = buildSet {
  fun Scope<*>.collect() {
    add(this)
    children.forEach { it.collect() }
  }

  root.collect()
}

fun <N : Any> Scope<*>.allScopesOf(name: KClass<N> = inject): Set<Scope<N>> =
  allScopes.filterTo(mutableSetOf()) { it.name == name }.cast()

fun <N : Any> Scope<*>.scopeOfOrNull(name: KClass<N> = inject): Scope<N>? =
  allScopesOf<N>().firstOrNull()

fun <N : Any> Scope<*>.scopeOf(name: KClass<N> = inject): Flow<Scope<N>> = snapshotFlow {
  scopeOfOrNull<N>()
}.filterNotNull()

fun <N : Any, T> Flow<T>.flowInScope(scope: Scope<*>, name: KClass<N> = inject): Flow<T> = channelFlow {
  scope.repeatInScope<N> {
    collect { send(it) }
  }
}

suspend fun <N : Any> Scope<*>.repeatInScope(name: KClass<N> = inject, block: suspend (Scope<N>) -> Unit) {
  val jobs = mutableMapOf<Scope<*>, Job>()
  try {
    snapshotFlow { allScopesOf<N>() }.collect { scopes ->
      jobs.keys.toList().forEach {
        if (it !in scopes)
          jobs.remove(it)?.cancel()
      }

      scopes.forEach { scope ->
        jobs.getOrPut(scope) {
          scope.coroutineScope.launch {
            block(scope)
          }
        }
      }
    }
  } finally {
    jobs.values.forEach { it.cancel() }
  }
}

val Scope<*>.root: Scope<*> get() = parent?.root ?: this

val Scope<*>.coroutineScope: CoroutineScope get() = service()

data class ProvidedService<N, T : Any>(val key: KClass<T>, val factory: () -> T) {
  @Provide companion object {
    @Provide fun <N> defaultServices() = emptyList<ProvidedService<N, *>>()
  }
}

@Stable interface ScopeObserver<N : Any> : ExtensionPoint<ScopeObserver<N>> {
  fun onEnter(scope: Scope<N>) {
  }

  fun onExit(scope: Scope<N>) {
  }

  @Provide companion object {
    @Provide fun <N : Any> defaultObservers() = emptyList<ScopeObserver<N>>()
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class Scoped<N> {
  @Provide companion object {
    @Provide inline fun <@AddOn T : @Scoped<N> S, reified S : Any, N : Any> scoped(
      scope: Scope<N>,
      key: TypeKey<S>,
      crossinline init: () -> T,
    ): S = scope.scoped(key) { init() }
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class Service<N> {
  @Provide companion object {
    @Provide fun <@AddOn T : @Service<N> S, S : Any, N> service(
      key: KClass<S>,
      factory: () -> T
    ) = ProvidedService<N, S>(key, factory)

    @Provide fun <@AddOn T : @Service<N> S, S : Any, N> accessor(service: T): S = service
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ScopedService<N> {
  @Provide companion object {
    @Provide inline fun <@AddOn T : @ScopedService<N> S, reified S : Any, N : Any> scoped(
      init: () -> T,
    ): @Scoped<N> S = init()

    @Provide inline fun <@AddOn T : @ScopedService<N> S, reified S : Any, N : Any> service(
      noinline init: () -> S,
    ) = ProvidedService<N, S>(S::class, init)
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class ParentScope

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class Eager<N : Any> {
  @Provide companion object {
    @Provide fun <@AddOn T : @Eager<N> S, S : Any, N : Any> scoped(value: T): @Scoped<N> S = value

    @Provide inline fun <@AddOn T : @Eager<N> S, S : Any, N : Any> observer(
      key: KClass<S>,
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

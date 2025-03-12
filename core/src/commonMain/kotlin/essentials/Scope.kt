/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import essentials.compose.launchMolecule
import injekt.*
import injekt.common.*
import kotlinx.atomicfu.locks.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Stable @Provide class Scope<N : Any>(
  val name: KClass<N>,
  val parent: ParentScope? = null,
  config: (Scope<N>, ParentScope?) -> ScopeConfig<N>
) : SynchronizedObject(), DisposableHandle {
  var isDisposed by mutableStateOf(false)
    private set

  @PublishedApi internal val cache = hashMapOf<Any, Any>()

  private val services: Map<KClass<*>, ProvidedService<*, *>>?

  var children by mutableStateOf(emptySet<Scope<*>>())
    private set

  init {
    parent?.registerChild(this)

    val config = config(this@Scope, this@Scope)
    services = if (config.services.isEmpty()) null
    else hashMapOf<KClass<*>, ProvidedService<*, *>>().apply {
      for (providedService in config.services)
        this[providedService.key] = providedService
    }

    config.initializers
      .sortedWithLoadingOrder()
      .fastForEach { it.instance.initialize() }

    startScopeComposition(config)
  }

  private fun startScopeComposition(config: ScopeConfig<N>) {
    val compositions = config.compositions
    if (compositions.isNotEmpty())
      coroutineScope.launchMolecule {
        compositions.fastForEachIndexed { i, composition ->
          key(i) { composition() }
        }
      }
  }

  fun <T : Any> serviceOrNull(key: KClass<T> = inject): T? = services?.get(key)?.let {
    return it.factory().unsafeCast()
  } ?: parent?.serviceOrNull(key)

  fun <T : Any> service(key: KClass<T> = inject): T =
    serviceOrNull(key) ?: error("No service found for ${key.qualifiedName} in ${name.qualifiedName}")

  inline fun <T> scoped(key: Any, compute: () -> T): T {
    cache[key]?.let { return (if (it !== NULL) it else null).unsafeCast() }
    return synchronized(cache) {
      checkDisposed()
      val value = cache.getOrPut(key) { compute() ?: NULL }
      (if (value !== NULL) value else null).unsafeCast()
    }
  }

  inline fun <T : Any> scoped(key: TypeKey<T> = inject, compute: () -> T): T =
    scoped(key.value, compute)

  override fun dispose() {
    synchronized(this) {
      if (!isDisposed) {
        children.forEach { it.dispose() }

        isDisposed = true

        parent?.unregisterChild(this)

        synchronized(cache) {
          cache.values.toList()
            .also { cache.clear() }
        }.fastForEach { cachedValue ->
          if (cachedValue is DisposableHandle)
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
    check(!isDisposed) { "Cannot use a disposed scope" }
  }

  @Provide companion object {
    @PublishedApi internal val NULL = Any()
  }
}

@Provide data class ScopeConfig<N : Any>(
  val initializers: List<ExtensionPointRecord<ScopeInitializer<N>>>,
  val compositions: List<@Composable () -> ScopeCompositionResult<N>>,
  val services: List<ProvidedService<N, *>>
)

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

@Tag annotation class Scoped<N> {
  @Provide companion object {
    @Provide inline fun <@AddOn T : @Scoped<N> S, reified S, N : Any> scoped(
      scope: Scope<N>,
      key: TypeKey<S>,
      crossinline init: () -> T,
    ): S = scope.scoped(key) { init() }
  }
}

@Tag annotation class Service<N> {
  @Provide companion object {
    @Provide fun <@AddOn T : @Service<N> S, S : Any, N> service(
      key: KClass<S>,
      factory: () -> T
    ) = ProvidedService<N, S>(key, factory)

    @Provide fun <@AddOn T : @Service<N> S, S : Any, N> accessor(service: T): S = service
  }
}

@Tag annotation class ScopedService<N> {
  @Provide companion object {
    @Provide inline fun <@AddOn T : @ScopedService<N> S, reified S : Any, N : Any> scoped(
      init: () -> T,
    ): @Scoped<N> S = init()

    @Provide inline fun <@AddOn T : @ScopedService<N> S, reified S : Any, N : Any> service(
      noinline init: () -> S,
    ) = ProvidedService<N, S>(S::class, init)
  }
}

@Tag typealias ParentScope = Scope<*>

@Tag annotation class Eager<N : Any> {
  @Provide companion object {
    @Provide fun <@AddOn T : @Eager<N> S, S, N : Any> scoped(value: T): @Scoped<N> S = value

    @Provide inline fun <@AddOn T : @Eager<N> S, S, N : Any> initializer(
      crossinline factory: () -> S
    ): ScopeInitializer<N> = ScopeInitializer { factory() }
  }
}

val LocalScope = compositionLocalOf<Scope<*>> { error("No provided scope") }

fun interface ScopeInitializer<N : Any> : ExtensionPoint<ScopeInitializer<N>> {
  fun initialize()
}

@Tag typealias ScopeCompositionResult<N> = Unit

@Provide fun <N> defaultScopeCompositions() =
  emptyList<@Composable () -> ScopeCompositionResult<N>>()

@Tag annotation class ComposeIn<N : Any> {
  @Provide companion object {
    @Provide @Composable inline fun <@AddOn T : @ComposeIn<N> S, S, N : Any> composeIn(
      scope: Scope<N>,
      key: TypeKey<StateFlow<S>>,
      crossinline block: @Composable () -> T,
    ): S = scope.scoped(key.value) {
      scope.coroutineScope.launchMolecule(
        RecompositionMode.ContextClock,
        AndroidUiDispatcher.Main,
        body = { block() }
      )
    }.collectAsState().value
  }
}

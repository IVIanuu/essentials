package com.ivianuu.essentials

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Provide @Scoped<AppScope> class ScopeManager {
  private val _activeScopes = MutableStateFlow<Set<Scope<*>>>(emptySet())
  val activeScopes: StateFlow<Set<Scope<*>>> by this::_activeScopes

  private val _observer = object : ScopeObserver<Any> {
    override fun onEnter(scope: Scope<Any>) = _activeScopes.update { it + scope }

    override fun onExit(scope: Scope<Any>) = _activeScopes.update { it - scope }
  }

  @Provide fun <N : Any> observer(): ScopeObserver<N> = _observer.cast()
}

fun <N : Any> ScopeManager.activeScopesOf(@Inject name: KClass<N>): Flow<Set<Scope<N>>> = activeScopes
  .map { it.filterTo(mutableSetOf()) { it.name == name }.cast() }

fun <N : Any> ScopeManager.scopeOfOrNull(@Inject name: KClass<N>): Flow<Scope<N>?> = activeScopesOf<N>()
  .map { it.firstOrNull() }

fun <N : Any> ScopeManager.scopeOf(@Inject name: KClass<N>): Flow<Scope<N>> = activeScopesOf<N>()
  .mapNotNull { it.firstOrNull() }

fun <N : Any, T> ScopeManager.flowInScope(flow: Flow<T>, @Inject name: KClass<N>): Flow<T> = channelFlow {
  repeatInScope<N> {
    flow.collect { send(it) }
  }
}

suspend fun <N : Any> ScopeManager.repeatInScope(@Inject name: KClass<N>, block: suspend (Scope<N>) -> Unit) {
  val jobs = mutableMapOf<Scope<*>, Job>()
  try {
    activeScopesOf<N>().collect { scopes ->
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

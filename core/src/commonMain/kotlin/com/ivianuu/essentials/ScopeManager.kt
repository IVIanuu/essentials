package com.ivianuu.essentials

import androidx.compose.runtime.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Provide @Scoped<AppScope> class ScopeManager {
  var activeScopes by mutableStateOf(emptySet<Scope<*>>())
    private set

  private val _observer = object : ScopeObserver<Any> {
    override fun onEnter(scope: Scope<Any>) {
      activeScopes += scope
    }

    override fun onExit(scope: Scope<Any>) {
      activeScopes -= scope
    }
  }

  @Provide fun <N : Any> observer(): ScopeObserver<N> = _observer.cast()
}

fun <N : Any> ScopeManager.activeScopesOf(name: KClass<N> = inject): Set<Scope<N>> =
  activeScopes.filterTo(mutableSetOf()) { it.name == name }.cast()

fun <N : Any> ScopeManager.scopeOfOrNull(name: KClass<N> = inject): Scope<N>? =
  activeScopesOf<N>().firstOrNull()

fun <N : Any> ScopeManager.scopeOf(name: KClass<N> = inject): Flow<Scope<N>> = snapshotFlow {
  scopeOfOrNull<N>()
}.filterNotNull()

fun <N : Any, T> ScopeManager.flowInScope(flow: Flow<T>, name: KClass<N> = inject): Flow<T> = channelFlow {
  repeatInScope<N> {
    flow.collect { send(it) }
  }
}

suspend fun <N : Any> ScopeManager.repeatInScope(name: KClass<N> = inject, block: suspend (Scope<N>) -> Unit) {
  val jobs = mutableMapOf<Scope<*>, Job>()
  try {
    snapshotFlow { activeScopesOf<N>() }.collect { scopes ->
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

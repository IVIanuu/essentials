package com.ivianuu.essentials

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Provide @Scoped<AppScope> class ScopeManager {
  val activeScopes = MutableStateFlow<Set<Scope<*>>>(emptySet())

  @Provide fun <N> observer(): ScopeObserver<N> = object : ScopeObserver<N> {
    override fun onEnter(scope: Scope<N>) = activeScopes.update { it + scope }

    override fun onExit(scope: Scope<N>) = activeScopes.update { it - scope }
  }
}

fun <N> ScopeManager.activeScopesOf(@Inject name: TypeKey<N>): Flow<Set<Scope<N>>> = activeScopes
  .map { it.filterTo(mutableSetOf()) { it.name == name }.cast() }

fun <N> ScopeManager.scopeOfOrNull(@Inject name: TypeKey<N>): Flow<Scope<N>?> = activeScopesOf<N>()
  .map { it.firstOrNull() }

fun <N> ScopeManager.scopeOf(@Inject name: TypeKey<N>): Flow<Scope<N>> = activeScopesOf<N>()
  .mapNotNull { it.firstOrNull() }

fun <N, T> ScopeManager.flowInScope(flow: Flow<T>, @Inject name: TypeKey<N>): Flow<T> = channelFlow {
  repeatInScope<N> {
    flow.collect { send(it) }
  }
}

suspend fun <N> ScopeManager.repeatInScope(@Inject name: TypeKey<N>, block: suspend (Scope<N>) -> Unit) {
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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.coroutineScope
import com.ivianuu.essentials.coroutines.guarantee
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.root
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ScopeManager {
  val activeScopes: StateFlow<Set<Scope<*>>>
}

suspend fun <N> ScopeManager.awaitFirstActiveScope(@Inject name: TypeKey<N>): Scope<N> =
  activeScopes<N>().mapNotNull { it.firstOrNull() }.first()

fun <N> ScopeManager.firstActiveScopeOrNull(@Inject name: TypeKey<N>): Scope<N>? =
  activeScopes.value.firstOrNull { it.name == name }?.cast()

fun <N> ScopeManager.activeScopes(@Inject name: TypeKey<N>): Flow<Set<Scope<N>>> = activeScopes
  .map { it.filterTo(mutableSetOf()) { it.name == name }.cast<Set<Scope<N>>>() }
  .distinctUntilChanged()

fun <N> ScopeManager.activeScope(@Inject name: TypeKey<N>): Flow<Scope<N>> = activeScopes<N>()
  .map { it.first() }

fun <N> ScopeManager.activeScopeOrNull(@Inject name: TypeKey<N>): Flow<Scope<N>?> = activeScopes<N>()
  .map { it.firstOrNull() }

fun <N, T> ScopeManager.flowInScope(flow: Flow<T>, @Inject name: TypeKey<N>): Flow<T> = channelFlow {
  repeatInScope<N> {
    flow.collect { send(it) }
  }
}

suspend fun <N> ScopeManager.repeatInScope(@Inject name: TypeKey<N>, block: suspend (Scope<N>) -> Unit) {
  val jobs = mutableMapOf<Scope<*>, Job>()
  guarantee(
    block = {
      activeScopes<N>().collect { scopes ->
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
    },
    finalizer = { _ -> jobs.values.forEach { it.cancel() } }
  )
}

@Provide class ScopeManagerImpl(scope: Scope<*>) : ScopeManager {
  private val _activeScopes = scope.root.scoped(ActiveScopesKey) {
    MutableStateFlow<Set<Scope<*>>>(emptySet())
  }
  override val activeScopes: StateFlow<Set<Scope<*>>> by this::_activeScopes

  @Provide fun <N> scopeHandler(scope: Scope<N>) = ScopeWorker<N> {
    _activeScopes.update { it + scope }
    onCancel { _activeScopes.update { it - scope } }
  }

  private object ActiveScopesKey
}

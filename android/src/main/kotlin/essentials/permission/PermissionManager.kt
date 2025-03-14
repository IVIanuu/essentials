/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.runtime.*
import androidx.compose.ui.util.*
import essentials.*
import essentials.compose.*
import essentials.coroutines.*
import essentials.logging.*
import essentials.ui.navigation.*
import essentials.util.launchUi
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Provide @Service<AppScope> data class PermissionDependencies(
  val permissions: Map<KClass<out Permission>, () -> Permission>,
  val stateProviders: Map<KClass<out Permission>, suspend (Permission) -> PermissionState<Permission>>
)

fun <T : Permission> KClass<T>.toPermission(scope: Scope<*> = inject): T =
  service<PermissionDependencies>().permissions[this]!!().unsafeCast()

fun List<KClass<out Permission>>.permissionState(scope: Scope<*> = inject): Flow<Boolean> = moleculeFlow {
  fastMap { permissionKey ->
    val permission = remember { permissionKey.toPermission() }
    val stateProvider = service<PermissionDependencies>()
      .stateProviders[permissionKey]!!
    produceState<Boolean?>(null) {
      permissionRefreshes
        .onStart<Any?> { this.emit(Unit) }
        .map {
          withContext(coroutineContexts().io) {
            stateProvider(permission)
          }
        }
        .collect { value = it }
    }
      .value
  }
    .takeIf { it.fastAll { it != null } }
    ?.fastAll { it == true }
}.filterNotNull()

suspend fun List<KClass<out Permission>>.ensure(scope: Scope<*> = inject): Boolean {
  d { "ensure permissions $this" }

  val result = fastAll { listOf(it).permissionState().first() } || run {
    navigator(launchUi()).push(PermissionRequestScreen(this@ensure)) == true
  }

  d { "ensure permissions result $this -> $result" }
  return result
}

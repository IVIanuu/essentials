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
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Stable @Provide class PermissionManager(
  private val uiLauncher: UiLauncher,
  @property:Provide private val scope: Scope<*> = inject,
  private val permissions: Map<KClass<out Permission>, () -> Permission>,
  private val stateProviders: Map<KClass<out Permission>, suspend (Permission) -> PermissionState<Permission>>
) {
  fun <T : Permission> permission(key: KClass<T>): T =
    permissions[key]!!().unsafeCast()

  fun permissionState(permissions: List<KClass<out Permission>>): Flow<Boolean> = moleculeFlow {
    permissions.fastMap { permissionKey ->
      val permission = remember { permission(permissionKey) }
      val stateProvider = stateProviders[permissionKey]!!
      produceState<Boolean?>(null) {
        permissionRefreshes
          .onStart<Any?> { emit(Unit) }
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

  suspend fun ensurePermissions(permissions: List<KClass<out Permission>>): Boolean {
    d { "ensure permissions $permissions" }

    val result = permissions.fastAll { permissionState(listOf(it)).first() } || run {
      navigator(uiLauncher.start()).push(PermissionRequestScreen(permissions)) == true
    }

    d { "ensure permissions result $permissions -> $result" }
    return result
  }
}

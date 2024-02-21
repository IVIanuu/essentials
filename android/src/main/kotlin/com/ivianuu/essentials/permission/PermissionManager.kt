/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Provide class PermissionManager(
  private val appUiStarter: AppUiStarter,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val permissions: Map<KClass<out Permission>, () -> Permission>,
  private val stateProviders: Map<KClass<out Permission>, () -> PermissionStateProvider<Permission>>
) {
  fun <T : Permission> permission(key: KClass<T>): T =
    permissions[key]!!().unsafeCast()

  fun permissionState(permissions: List<KClass<out Permission>>): Flow<Boolean> =
    if (permissions.isEmpty()) flowOf(true)
    else combine(
      permissions
        .map { permissionKey ->
          val permission = this.permissions[permissionKey]!!()
          val stateProvider = stateProviders[permissionKey]!!()
          permissionRefreshes
            .onStart<Any?> { emit(Unit) }
            .map {
              withContext(coroutineContexts.io) {
                stateProvider(permission)
              }
            }
        }
    ) { states -> states.all { it } }

  suspend fun requestPermissions(permissions: List<KClass<out Permission>>): Boolean {
    logger.d { "request permissions $permissions" }

    val result = permissions.all { permissionState(listOf(it)).first() } || run {
      appUiStarter()
        .cast<UiScopeOwner>()
        .uiScope
        .navigator
        .push(PermissionRequestScreen(permissions)) == true
    }

    logger.d { "request permissions result $permissions -> $result" }
    return result
  }
}

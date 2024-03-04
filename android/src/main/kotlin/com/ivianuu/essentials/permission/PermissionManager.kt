/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import androidx.compose.runtime.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
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

  fun permissionState(permissions: List<KClass<out Permission>>): Flow<Boolean> = moleculeFlow {
    permissions.map { permissionKey ->
      val permission = remember { this.permissions[permissionKey]!!() }
      val stateProvider = remember { stateProviders[permissionKey]!!() }
      permissionRefreshes
        .onStart<Any?> { emit(Unit) }
        .map {
          withContext(coroutineContexts.io) {
            stateProvider.permissionState(permission)
              .also { println("lolo check $permissions $it") }
          }
        }
        .state(null)
    }
      .takeIf { it.all { it != null } }
      ?.all { it == true }
  }.filterNotNull()

  suspend fun requestPermissions(permissions: List<KClass<out Permission>>): Boolean {
    logger.d { "request permissions $permissions" }

    val result = permissions.all { permissionState(listOf(it)).first() } || run {
      appUiStarter.startAppUi()
        .cast<UiScopeOwner>()
        .uiScope
        .navigator
        .push(PermissionRequestScreen(permissions)) == true
    }

    logger.d { "request permissions result $permissions -> $result" }
    return result
  }
}

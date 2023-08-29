/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.permission.ui.PermissionRequestScreen
import com.ivianuu.essentials.ui.UiScopeOwner
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.unsafeCast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface PermissionManager {
  fun <T : Permission> permission(key: TypeKey<T>): T

  fun permissionState(permissions: List<TypeKey<Permission>>): Flow<Boolean>

  suspend fun requestPermissions(permissions: List<TypeKey<Permission>>): Boolean
}

@Provide class PermissionManagerImpl(
  private val appUiStarter: AppUiStarter,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val permissions: Map<TypeKey<Permission>, () -> Permission>,
  private val stateProviders: Map<TypeKey<Permission>, () -> PermissionStateProvider<Permission>>
) : PermissionManager {
  override fun <T : Permission> permission(key: TypeKey<T>): T =
    permissions[key]!!().unsafeCast()

  override fun permissionState(permissions: List<TypeKey<Permission>>): Flow<Boolean> =
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

  override suspend fun requestPermissions(permissions: List<TypeKey<Permission>>): Boolean {
    logger.log { "request permissions $permissions" }

    val result = permissions.all { permissionState(listOf(it)).first() } ||
        appUiStarter()
          .cast<UiScopeOwner>()
          .uiScope
          .navigator
          .push(PermissionRequestScreen(permissions)) == true

    logger.log { "request permissions result $permissions -> $result" }
    return result
  }
}
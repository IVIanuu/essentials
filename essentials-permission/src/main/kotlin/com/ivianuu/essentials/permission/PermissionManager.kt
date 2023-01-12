/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.permission.ui.PermissionRequestKey
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.unsafeCast
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.flow.Flow
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

context(AppUiStarter, Logger) @Provide class PermissionManagerImpl(
  private val context: DefaultContext,
  private val navigator: Navigator,
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
              withContext(context) {
                stateProvider.isPermissionGranted(permission)
              }
            }
        }
    ).map { states -> states.all { it } }

  override suspend fun requestPermissions(permissions: List<TypeKey<Permission>>): Boolean =
    withContext(context) {
      log { "request permissions $permissions" }

      if (permissions.all { permissionState(listOf(it)).first() })
        return@withContext true

      startAppUi()

      val result = navigator.push(PermissionRequestKey(permissions)) == true
      log { "request permissions result $permissions -> $result" }
      return@withContext result
    }
}

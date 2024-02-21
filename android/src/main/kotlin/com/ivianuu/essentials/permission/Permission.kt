/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import androidx.compose.runtime.*
import arrow.fx.coroutines.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

interface Permission {
  val title: String
  val desc: String? get() = null
  val icon: (@Composable () -> Unit)? get() = null
}

@Provide object PermissionModule {
  @Provide fun <@Spread T : Permission> permission(
    key: KClass<T>,
    permission: () -> T
  ): Pair<KClass<Permission>, () -> Permission> = (key to permission).cast()

  @Provide val defaultPermissions get() = emptyList<Pair<KClass<Permission>, () -> Permission>>()

  @Provide fun <@Spread T : Permission> requestHandlerBinding(
    key: KClass<T>,
    requestHandler: () -> PermissionRequestHandler<T>
  ): Pair<KClass<Permission>, () -> PermissionRequestHandler<Permission>> =
    (key to { requestHandler().intercept() }).cast()

  @Provide val defaultRequestHandlers get() = emptyList<Pair<KClass<Permission>, () -> PermissionRequestHandler<Permission>>>()

  @Provide fun <@Spread T : Permission> stateProvider(
    key: KClass<T>,
    stateProvider: () -> PermissionStateProvider<T>
  ): Pair<KClass<Permission>, () -> PermissionStateProvider<Permission>> =
    (key to stateProvider).cast()

  @Provide val defaultStateProviders get() = emptyList<Pair<KClass<Permission>, () -> PermissionStateProvider<Permission>>>()
}

fun interface PermissionStateProvider<P : Permission> {
  suspend operator fun invoke(permission: P): Boolean
}

fun interface PermissionRequestHandler<P : Permission> {
  suspend operator fun invoke(permission: P)
}

internal val permissionRefreshes = EventFlow<Unit>()

@Provide fun permissionRefreshesWorker() = ScopeWorker<UiScope> {
  permissionRefreshes.emit(Unit)
}

private fun <P : Permission> PermissionRequestHandler<P>.intercept() = PermissionRequestHandler<P> {
  this(it)
  permissionRefreshes.emit(Unit)
}

interface PermissionRevokeHandler : suspend (List<KClass<Permission>>) -> Unit {
  val permissions: List<KClass<Permission>>

  @Provide companion object {
    inline operator fun invoke(
      permissions: List<KClass<Permission>>,
      crossinline block: suspend (List<KClass<Permission>>) -> Unit
    ): PermissionRevokeHandler {
      return object : PermissionRevokeHandler {
        override val permissions: List<KClass<Permission>>
          get() = permissions

        override suspend fun invoke(p1: List<KClass<Permission>>) {
          block(p1)
        }
      }
    }

    @Provide val defaultHandlers get() = emptyList<PermissionRevokeHandler>()
  }
}

@Provide fun permissionRevokeWorker(
  handlers: List<PermissionRevokeHandler>,
  permissionManager: PermissionManager
) = ScopeWorker<UiScope> {
  handlers.parMap { handler ->
    val revokedPermissions = handler.permissions
      .filter { !permissionManager.permissionState(listOf(it)).first() }
    if (revokedPermissions.isNotEmpty())
      handler(revokedPermissions)
  }
}

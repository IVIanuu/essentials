/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.runtime.*
import androidx.compose.ui.util.fastFilter
import arrow.fx.coroutines.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import essentials.ui.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

interface Permission {
  val title: String
  val desc: String? get() = null
  val icon: (@Composable () -> Unit)? get() = null
}

@Provide object PermissionModule {
  @Provide fun <@AddOn T : Permission> permission(
    key: KClass<T>,
    permission: () -> T
  ): Pair<KClass<out Permission>, () -> Permission> = (key to permission).cast()

  @Provide val defaultPermissions get() = emptyList<Pair<KClass<out Permission>, () -> Permission>>()

  @Provide fun <@AddOn T : Permission> requestHandlerBinding(
    key: KClass<T>,
    requestHandler: () -> PermissionRequestHandler<T>
  ): Pair<KClass<out Permission>, () -> PermissionRequestHandler<Permission>> =
    (key to { requestHandler().intercept() }).cast()

  @Provide val defaultRequestHandlers get() = emptyList<Pair<KClass<out Permission>, () -> PermissionRequestHandler<Permission>>>()

  @Provide fun <@AddOn T : Permission> stateProvider(
    key: KClass<T>,
    stateProvider: () -> PermissionStateProvider<T>
  ): Pair<KClass<out Permission>, () -> PermissionStateProvider<Permission>> =
    (key to stateProvider).cast()

  @Provide val defaultStateProviders get() = emptyList<Pair<KClass<out Permission>, () -> PermissionStateProvider<Permission>>>()
}

fun interface PermissionStateProvider<P : Permission> {
  suspend fun permissionState(permission: P): Boolean
}

fun interface PermissionRequestHandler<P : Permission> {
  suspend fun requestPermission(permission: P)
}

internal val permissionRefreshes = EventFlow<Unit>()

@Provide fun permissionRefreshesWorker() = ScopeInitializer<UiScope> {
  permissionRefreshes.tryEmit(Unit)
}

private fun <P : Permission> PermissionRequestHandler<P>.intercept() = PermissionRequestHandler<P> {
  requestPermission(it)
  permissionRefreshes.emit(Unit)
}

interface PermissionRevokeHandler {
  val permissions: List<KClass<out Permission>>

  suspend fun onPermissionRevoked(permissions: List<KClass<out Permission>>)

  @Provide companion object {
    inline operator fun invoke(
      permissions: List<KClass<out Permission>>,
      crossinline onPermissionRevoked: suspend (List<KClass<out Permission>>) -> Unit
    ): PermissionRevokeHandler {
      return object : PermissionRevokeHandler {
        override val permissions: List<KClass<out Permission>>
          get() = permissions

        override suspend fun onPermissionRevoked(permissions: List<KClass<out Permission>>) {
          onPermissionRevoked.invoke(permissions)
        }
      }
    }

    @Provide val defaultHandlers get() = emptyList<PermissionRevokeHandler>()
  }
}

@Provide fun permissionRevokeWorker(
  handlers: List<PermissionRevokeHandler>,
  permissionManager: PermissionManager
) = ScopeComposition<UiScope> {
  LaunchedEffect(true) {
    handlers.parMap { handler ->
      val revokedPermissions = handler.permissions
        .fastFilter { !permissionManager.permissionState(listOf(it)).first() }
      if (revokedPermissions.isNotEmpty())
        handler.onPermissionRevoked(revokedPermissions)
    }
  }
}

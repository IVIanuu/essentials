/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import androidx.compose.runtime.Composable
import arrow.fx.coroutines.parMap
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.first

interface Permission {
  val title: String
  val desc: String? get() = null
  val icon: Icon? get() = null

  interface Icon {
    @Composable operator fun invoke()

    @Provide companion object {
      inline operator fun invoke(crossinline icon: @Composable () -> Unit) = object : Icon {
        @Composable override fun invoke() {
          icon()
        }
      }
    }
  }
}

@Provide object PermissionModule {
  @Provide fun <@Spread T : Permission> permission(
    permissionKey: TypeKey<T>,
    permission: () -> T
  ): Pair<TypeKey<Permission>, () -> Permission> = permissionKey to permission

  @Provide val defaultPermissions get() = emptyList<Pair<TypeKey<Permission>, () -> Permission>>()

  @Provide fun <@Spread T : Permission> requestHandlerBinding(
    permissionKey: TypeKey<T>,
    requestHandler: () -> PermissionRequestHandler<T>
  ): Pair<TypeKey<Permission>, () -> PermissionRequestHandler<Permission>> =
    (permissionKey to { requestHandler().intercept() }).cast()

  @Provide val defaultRequestHandlers get() = emptyList<Pair<TypeKey<Permission>, () -> PermissionRequestHandler<Permission>>>()

  @Provide fun <@Spread T : Permission> stateProvider(
    permissionKey: TypeKey<T>,
    stateProvider: () -> PermissionStateProvider<T>
  ): Pair<TypeKey<Permission>, () -> PermissionStateProvider<Permission>> =
    (permissionKey to stateProvider).cast()

  @Provide val defaultStateProviders get() = emptyList<Pair<TypeKey<Permission>, () -> PermissionStateProvider<Permission>>>()
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

interface PermissionRevokeHandler : suspend (List<TypeKey<Permission>>) -> Unit {
  val permissions: List<TypeKey<Permission>>

  @Provide companion object {
    inline operator fun invoke(
      permissions: List<TypeKey<Permission>>,
      crossinline block: suspend (List<TypeKey<Permission>>) -> Unit
    ): PermissionRevokeHandler {
      return object : PermissionRevokeHandler {
        override val permissions: List<TypeKey<Permission>>
          get() = permissions

        override suspend fun invoke(p1: List<TypeKey<Permission>>) {
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
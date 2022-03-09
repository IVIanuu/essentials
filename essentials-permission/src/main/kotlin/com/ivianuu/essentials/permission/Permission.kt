/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import androidx.compose.runtime.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.permission.ui.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface Permission {
  val title: String
  val desc: String? get() = null
  @Composable fun Icon()
}

@Provide class PermissionModule<@Spread T : Permission> {
  @Provide fun permissionSetElement(
    permissionKey: TypeKey<T>,
    permission: T
  ): Pair<TypeKey<Permission>, Permission> = permissionKey to permission

  @Provide fun requestHandler(
    permissionKey: TypeKey<T>,
    requestHandler: PermissionRequestHandler<T>
  ): Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>> =
    (permissionKey to requestHandler.intercept()) as Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>>

  @Provide fun permissionState(
    permissionKey: TypeKey<T>,
    state: Flow<PermissionState<T>>
  ): Pair<TypeKey<Permission>, Flow<PermissionState<Permission>>> = permissionKey to state

  companion object {
    @Provide val defaultPermissions: Collection<Pair<TypeKey<Permission>, Permission>>
      get() = emptyList()

    @Provide val defaultRequestHandlers: Collection<Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>>>
      get() = emptyList()

    @Provide val defaultStates: Collection<Pair<TypeKey<Permission>, Flow<PermissionState<Permission>>>>
      get() = emptyList()
  }
}

fun interface PermissionStateProvider<P : Permission> : suspend (P) -> Boolean

fun interface PermissionRequestHandler<P : Permission> : suspend (P) -> Unit

@Tag annotation class PermissionStateTag<P>
typealias PermissionState<P> = @PermissionStateTag<P> Boolean

@Provide fun <P : Permission> permissionState(
  context: DefaultContext,
  permission: P,
  stateProvider: PermissionStateProvider<P>
): Flow<PermissionState<P>> = permissionRefreshes
  .onStart<Any?> { emit(Unit) }
  .map {
    withContext(context) {
      stateProvider(permission)
    }
  }

fun interface PermissionStateFactory : (List<TypeKey<Permission>>) -> Flow<PermissionState<Boolean>>

@Provide fun permissionStateFactory(
  permissionStates: () -> Map<TypeKey<Permission>, Flow<PermissionState<Permission>>>
) = PermissionStateFactory { permissions ->
  if (permissions.isEmpty()) flowOf(true)
  else combine(
    *permissions
      .map { permissionStates()[it]!! }
      .toTypedArray()
  ) { states -> states.all { it } }
}

internal val permissionRefreshes = EventFlow<Unit>()

@Provide fun permissionRefreshesWorker() = ScopeWorker<UiScope> {
  permissionRefreshes.emit(Unit)
}

private fun <P : Permission> PermissionRequestHandler<P>.intercept() = PermissionRequestHandler<P> {
  this(it)
  permissionRefreshes.emit(Unit)
}

fun interface PermissionRequester : suspend (List<TypeKey<Permission>>) -> Boolean

@Provide fun permissionRequester(
  appUiStarter: AppUiStarter,
  context: DefaultContext,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory,
  L: Logger
) = PermissionRequester { requestedPermissions ->
  withContext(context) {
    log { "request permissions $requestedPermissions" }

    if (requestedPermissions.all { permissionStateFactory(listOf(it)).first() })
      return@withContext true

    appUiStarter()

    val result = navigator.push(PermissionRequestKey(requestedPermissions)) == true
    log { "request permissions result $requestedPermissions -> $result" }
    return@withContext result
  }
}

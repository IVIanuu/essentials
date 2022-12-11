/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.permission.ui.PermissionRequestKey
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface Permission {
  val title: String
  val desc: String? get() = null
  val icon: (@Composable () -> Unit)? get() = null
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

fun interface PermissionStateFactory : (List<TypeKey<Permission>>) -> Flow<PermissionState<*>>

@Provide fun permissionStateFactory(
  permissionStates: () -> Map<TypeKey<Permission>, Flow<PermissionState<Permission>>>
) = PermissionStateFactory { permissions ->
  if (permissions.isEmpty()) flowOf(true)
  else combine(
    permissions
      .map { permissionStates()[it]!! }
  ).map { states -> states.all { it } }
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

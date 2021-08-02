/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.permission

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.permission.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface Permission {
  val title: String
  val desc: String? get() = null
  val icon: @Composable (() -> Unit)?// get() = null // todo uncomment default value once fixed
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
    (permissionKey to requestHandler.intercept()).cast()

  @Provide fun permissionState(
    permissionKey: TypeKey<T>,
    state: Flow<PermissionState<T>>
  ): Pair<TypeKey<Permission>, Flow<PermissionState<Permission>>> = permissionKey to state
}

typealias PermissionStateProvider<P> = suspend (P) -> Boolean

typealias PermissionRequestHandler<P> = suspend (P) -> Unit

typealias PermissionState<P> = Boolean

@Provide fun <P : Permission> permissionState(
  dispatcher: DefaultDispatcher,
  permission: P,
  stateProvider: PermissionStateProvider<P>
): Flow<PermissionState<P>> = permissionRefreshes
  .map { Unit }
  .onStart { emit(Unit) }
  .map {
    withContext(dispatcher) {
      stateProvider(permission)
    }
  }

typealias PermissionStateFactory = (List<TypeKey<Permission>>) -> Flow<PermissionState<Boolean>>

@Provide fun permissionStateFactory(
  permissionStates: Map<TypeKey<Permission>, Flow<PermissionState<Permission>>> = emptyMap()
): PermissionStateFactory = { permissions ->
  combine(
    *permissions
      .map { permissionStates[it]!! }
      .toTypedArray()
  ) { states -> states.all { it } }
}

internal val permissionRefreshes = EventFlow<Unit>()

@Provide fun <S : Scope> permissionRefreshesWorker(): ScopeWorker<S> = {
  permissionRefreshes.tryEmit(Unit)
  runOnCancellation { permissionRefreshes.tryEmit(Unit) }
}

private fun <P> PermissionRequestHandler<P>.intercept(): PermissionRequestHandler<P> = {
  this(it)
  permissionRefreshes.tryEmit(Unit)
}

typealias PermissionRequester = suspend (List<TypeKey<Permission>>) -> Boolean

@Provide fun permissionRequester(
  appUiStarter: AppUiStarter,
  dispatcher: DefaultDispatcher,
  logger: Logger,
  navigator: Navigator,
  permissionStateFactory: PermissionStateFactory
): PermissionRequester = { requestedPermissions ->
  withContext(dispatcher) {
    d { "request permissions $requestedPermissions" }

    if (requestedPermissions.all { permissionStateFactory(listOf(it)).first() })
      return@withContext true

    appUiStarter()

    val result = navigator.push(PermissionRequestKey(requestedPermissions)) == true
    d { "request permissions result $requestedPermissions -> $result" }
    return@withContext result
  }
}

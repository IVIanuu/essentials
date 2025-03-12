/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import androidx.compose.runtime.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.*
import injekt.*
import kotlin.reflect.*

interface Permission {
  val title: String
  val desc: String? get() = null
  val icon: (@Composable () -> Unit)? get() = null
}

@Provide object PermissionProviders {
  @Provide fun <@AddOn T : Permission> permission(
    key: KClass<T>,
    permission: () -> T
  ): Pair<KClass<out Permission>, () -> Permission> = (key to permission).cast()

  @Provide val defaultPermissions
    get() = emptyList<Pair<KClass<out Permission>, () -> Permission>>()

  @Provide fun <@AddOn T : Permission> requestHandlerBinding(
    key: KClass<T>,
    requestHandler: suspend (T) -> PermissionRequestResult<T>
  ): Pair<KClass<out Permission>, suspend (Permission) -> PermissionRequestResult<Permission>> =
    (key to requestHandler).cast()

  @Provide val defaultRequestHandlers
    get() = emptyList<Pair<KClass<out Permission>, suspend (Permission) -> PermissionRequestResult<Permission>>>()

  @Provide fun <@AddOn T : Permission> stateProvider(
    key: KClass<T>,
    stateProvider: suspend (T) -> PermissionState<T>
  ): Pair<KClass<out Permission>, suspend (Permission) -> PermissionState<Permission>> =
    (key to stateProvider).cast()

  @Provide val defaultStateProviders
    get() = emptyList<Pair<KClass<out Permission>, suspend (Permission) -> PermissionState<Permission>>>()
}

@Tag typealias PermissionState<P> = Boolean

@Tag typealias PermissionRequestResult<P> = Unit

internal val permissionRefreshes = EventFlow<Unit>()

@Provide fun permissionRefreshesWorker() = ScopeInitializer<UiScope> {
  permissionRefreshes.tryEmit(Unit)
}

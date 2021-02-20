/*
 * Copyright 2020 Manuel Wrage
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

import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.memo.memoize
import com.ivianuu.essentials.permission.ui.PermissionRequestKey
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Interceptor
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForKey
import com.ivianuu.injekt.common.Key
import com.ivianuu.injekt.common.keyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface Permission

@Qualifier
annotation class PermissionBinding

@Suppress("UNCHECKED_CAST")
@Macro
@GivenSetElement
fun <T : @PermissionBinding P, @ForKey P : Permission>
        permissionBindingImpl(
    @Given permission: T,
    @Given requestHandler: PermissionRequestHandler<P>,
    @Given stateProvider: PermissionStateProvider<P>
): PermissionElement<P> = PermissionElement(keyOf<P>(), permission, requestHandler, stateProvider)

typealias PermissionStateProvider<P> = suspend (P) -> Boolean

typealias PermissionRequestHandler<P> = suspend (P) -> Unit

data class PermissionElement<P>(
    val permissionKey: Key<P>,
    val permission: P,
    val requestHandler: PermissionRequestHandler<P>,
    val stateProvider: PermissionStateProvider<P>
)

typealias PermissionState<P> = Flow<Boolean>

@Given
fun <@ForKey P : Permission> permissionState(
    @Given stateFactory: PermissionStateFactory
): PermissionState<P> = stateFactory(keyOf<P>())

typealias PermissionStateFactory = (Key<Permission>) -> Flow<Boolean>

@Given
fun permissionStateFactory(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given permissions: Map<Key<Permission>, Permission>,
    @Given stateProviders: Map<Key<Permission>, PermissionStateProvider<Permission>>
): PermissionStateFactory = { permissionKey: Key<Permission> ->
    val stateProvider = stateProviders[permissionKey]!!
    val permission = permissions[permissionKey]!!
    permissionChanges
        .map { Unit }
        .onStart { emit(Unit) }
        .map {
            withContext(defaultDispatcher) {
                stateProvider(permission)
            }
        }.distinctUntilChanged()
}.memoize()

internal val permissionChanges = EventFlow<Unit>()

@Interceptor
fun <T : PermissionRequestHandler<P>, P : Permission> permissionRequestHandlerInterceptor(
    factory: () -> T
): T {
    val unintercepted = factory()
    val intercepted: PermissionRequestHandler<P> = { permission ->
        unintercepted(permission)
        permissionChanges.emit(Unit)
    }

    @Suppress("UNCHECKED_CAST")
    return intercepted as T
}

typealias PermissionRequester = suspend (List<Key<Permission>>) -> Boolean

@Given
fun permissionRequester(
    @Given appUiStarter: AppUiStarter,
    @Given defaultDispatcher: DefaultDispatcher,
    @Given logger: Logger,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given permissionStateFactory: PermissionStateFactory
): PermissionRequester = { requestedPermissions ->
    withContext(defaultDispatcher) {
        logger.d { "request requestedPermissions $requestedPermissions" }

        if (requestedPermissions.all { permissionStateFactory(it).first() })
            return@withContext true

        val key = PermissionRequestKey(requestedPermissions)
        appUiStarter()
        navigator(Push(key))

        return@withContext requestedPermissions.all { permissionStateFactory(it).first() }
    }
}

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

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.permission.ui.PermissionRequestKey
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pushForResult
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.GivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

interface Permission {
    val title: String
    val desc: String? get() = null
    val icon: @Composable (() -> Unit)?// get() = null // todo uncomment default value once fixed
}

class PermissionModule<T : P, @ForTypeKey P : Permission> {
    @Given
    fun permission(@Given permission: T): P = permission

    @Suppress("UNCHECKED_CAST")
    @Given
    fun permissionSetElement(
        @Given permission: T
    ): Pair<TypeKey<Permission>, Permission> = typeKeyOf<P>() to permission

    @Suppress("UNCHECKED_CAST")
    @Given
    fun requestHandler(
        @Given requestHandler: PermissionRequestHandler<P>
    ): Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>> =
        (typeKeyOf<P>() to requestHandler.intercept()) as Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>>

    @Suppress("UNCHECKED_CAST")
    @Given
    fun permissionState(
        @Given state: Flow<PermissionState<P>>
    ): Pair<TypeKey<Permission>, Flow<PermissionState<Permission>>> = typeKeyOf<P>() to state
}

@Qualifier
annotation class PermissionBinding

@Given
fun <@Given T : @PermissionBinding P, @ForTypeKey P : Permission> permissionModule(
): PermissionModule<T, P> = PermissionModule()

typealias PermissionStateProvider<P> = suspend (P) -> Boolean

typealias PermissionRequestHandler<P> = suspend (P) -> Unit

typealias PermissionState<P> = Boolean

@Given
fun <@ForTypeKey P : Permission> permissionState(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given permission: P,
    @Given stateProvider: PermissionStateProvider<P>
): Flow<PermissionState<P>> = permissionRefreshes
    .map { Unit }
    .onStart { emit(Unit) }
    .map {
        withContext(defaultDispatcher) {
            stateProvider(permission)
        }
    }

typealias PermissionStateFactory = (List<TypeKey<Permission>>) -> Flow<PermissionState<Boolean>>

@Given
fun permissionStateFactory(
    @Given permissionStates: Map<TypeKey<Permission>, Flow<PermissionState<Permission>>>
): PermissionStateFactory = { permissions ->
    combine(
        *permissions
            .map { permissionStates[it]!! }
            .toTypedArray()
    ) { states -> states.all { it } }
}

internal val permissionRefreshes = EventFlow<Unit>()

@Given
fun <S : GivenScope> permissionRefreshesWorker(): ScopeWorker<S> = {
    permissionRefreshes.tryEmit(Unit)
    runOnCancellation { permissionRefreshes.tryEmit(Unit) }
}

private fun <P> PermissionRequestHandler<P>.intercept(): PermissionRequestHandler<P> {
    return {
        this(it)
        permissionRefreshes.tryEmit(Unit)
    }
}

typealias PermissionRequester = suspend (List<TypeKey<Permission>>) -> Boolean

@Given
fun permissionRequester(
    @Given appUiStarter: AppUiStarter,
    @Given defaultDispatcher: DefaultDispatcher,
    @Given logger: Logger,
    @Given navigator: Collector<NavigationAction>,
    @Given permissionStateFactory: PermissionStateFactory
): PermissionRequester = { requestedPermissions ->
    withContext(defaultDispatcher) {
        logger.d { "request permissions $requestedPermissions" }

        if (requestedPermissions.all { permissionStateFactory(listOf(it)).first() })
            return@withContext true

        appUiStarter()

        val result = navigator.pushForResult(
            PermissionRequestKey(requestedPermissions)) == true
        logger.d { "request permissions result $requestedPermissions -> $result" }
        return@withContext result
    }
}

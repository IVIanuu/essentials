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
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.deferredFlow
import com.ivianuu.essentials.permission.ui.PermissionRequestKey
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.Push
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

interface Permission {
    val title: String
    val desc: String? get() = null
    val icon: @Composable (() -> Unit)?// get() = null // todo uncomment default value once fixed
}

class PermissionBindingModule<T : P, P : Permission>(private val permissionKey: TypeKey<P>) {

    @Given
    fun permission(@Given permission: T): P = permission

    @Suppress("UNCHECKED_CAST")
    @GivenSetElement
    fun permissionIntoSet(
        @Given permission: T
    ): Pair<TypeKey<Permission>, Permission> =
        (permissionKey to permission) as Pair<TypeKey<Permission>, Permission>

    @Suppress("UNCHECKED_CAST")
    @GivenSetElement
    fun requestHandlerIntoSet(
        @Given requestHandler: PermissionRequestHandler<P>
    ): Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>> =
        (permissionKey to requestHandler.intercept<P>()) as Pair<TypeKey<Permission>, PermissionRequestHandler<Permission>>

    @Suppress("UNCHECKED_CAST")
    @GivenSetElement
    fun permissionStateIntoSet(
        @Given state: PermissionState<P>
    ): Pair<TypeKey<Permission>, PermissionState<Permission>> =
        (permissionKey to state) as Pair<TypeKey<Permission>, PermissionState<Permission>>

}

@Qualifier
annotation class PermissionBinding

@Macro
@Module
fun <T : @PermissionBinding P, @ForTypeKey P : Permission> permissionBindingImpl(): PermissionBindingModule<T, P> =
    PermissionBindingModule(typeKeyOf())

typealias PermissionStateProvider<P> = suspend (P) -> Boolean

typealias PermissionRequestHandler<P> = suspend (P) -> Unit

typealias PermissionState<P> = Flow<Boolean>

@Given
fun <@ForTypeKey P : Permission> permissionState(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given permission: P,
    @Given stateProvider: PermissionStateProvider<P>
): PermissionState<P> = deferredFlow {
    permissionChanges
        .map { Unit }
        .onStart { emit(Unit) }
        .map {
            withContext(defaultDispatcher) {
                stateProvider(permission)
            }
        }
        .distinctUntilChanged()
}

typealias PermissionStateFactory = (List<TypeKey<Permission>>) -> PermissionState<Boolean>

@Given
fun permissionStateFactory(
    @Given permissionStates: Map<TypeKey<Permission>, PermissionState<Permission>>
): PermissionStateFactory = { permissions ->
    combine(
        *permissions
            .map { permissionStates[it]!! }
            .toTypedArray()
    ) { it.all { it } }
}

internal val permissionChanges = EventFlow<Unit>()

private fun <P> PermissionRequestHandler<P>.intercept(): PermissionRequestHandler<P> {
    return {
        this(it)
        permissionChanges.emit(Unit)
    }
}

typealias PermissionRequester = suspend (List<TypeKey<Permission>>) -> Boolean

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

        if (requestedPermissions.all { permissionStateFactory(listOf(it)).first() })
            return@withContext true

        val key = PermissionRequestKey(requestedPermissions)
        appUiStarter()
        navigator(Push(key))

        return@withContext requestedPermissions.all { permissionStateFactory(listOf(it)).first() }
    }
}

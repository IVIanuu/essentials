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

package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule
import kotlinx.coroutines.flow.Flow

@Immutable
data class Action(
    val key: String,
    val title: String,
    val permissions: List<Permission> = emptyList(),
    val unlockScreen: Boolean = false,
    val enabled: Boolean = true,
    val icon: ActionIcon,
    val execute: suspend () -> Unit
)

typealias ActionIcon = Flow<@Composable () -> Unit>

@BindingModule(ApplicationComponent::class)
annotation class ActionBinding {
    @Module
    class ModuleImpl<T : Action> {
        @SetElements
        fun actionIntoSet(instance: T): Set<Action> = setOf(instance)
    }
}

typealias choosePermissions = (ActionPermissions.() -> List<Permission>) -> List<Permission>

@Binding
fun choosePermissions(permissions: ActionPermissions): choosePermissions = { block ->
    permissions.block()
}

internal operator fun Permission.plus(other: Permission) = listOf(this, other)

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
}

@BindingModule(ApplicationComponent::class)
annotation class ActionFactoryBinding {
    @Module
    class ModuleImpl<T : ActionFactory> {
        @SetElements
        fun actionFactoryIntoSet(instance: T): Set<ActionFactory> = setOf(instance)
    }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(): ActionPickerResult?
}

@BindingModule(ApplicationComponent::class)
annotation class ActionPickerDelegateBinding {
    @Module
    class ModuleImpl<T : ActionPickerDelegate> {
        @SetElements
        fun actionPickerDelegate(instance: T): Set<ActionPickerDelegate> = setOf(instance)
    }
}

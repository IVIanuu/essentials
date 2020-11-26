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
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.MapEntries
import com.ivianuu.injekt.SetElements
import kotlinx.coroutines.flow.Flow

data class Action(
    val key: String,
    val title: String,
    val permissions: List<Permission> = emptyList(),
    val unlockScreen: Boolean = false,
    val enabled: Boolean = true,
    val icon: ActionIcon,
)

typealias ActionIcon = Flow<@Composable () -> Unit>

@Effect
annotation class ActionBinding(val key: String) {
    companion object {
        @MapEntries
        fun <T : Action> actionIntoSet(
            @Arg("key") key: String,
            provider: () -> @ForEffect T,
        ): Map<String, () -> Action> = mapOf(key to provider)
    }
}

typealias ActionExecutor = suspend () -> Unit

@Effect
annotation class ActionExecutorBinding(val key: String) {
    companion object {
        @MapEntries
        fun <T : ActionExecutor> actionExecutorIntoMap(
            @Arg("key") key: String,
            instance: @ForEffect T,
        ): Map<String, ActionExecutor> = mapOf(key to instance)
    }
}

@FunBinding
fun choosePermissions(
    permissions: ActionPermissions,
    @FunApi chooser: ActionPermissions.() -> List<Permission>,
): List<Permission> = permissions.chooser()

internal operator fun Permission.plus(other: Permission) = listOf(this, other)

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
    suspend fun createExecutor(key: String): ActionExecutor
}

@Effect
annotation class ActionFactoryBinding {
    companion object {
        @SetElements
        fun <T : ActionFactory> actionFactoryIntoSet(instance: @ForEffect T): Set<ActionFactory> = setOf(instance)
    }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(): ActionPickerResult?
}

@Effect
annotation class ActionPickerDelegateBinding {
    companion object {
        @SetElements
        fun <T : ActionPickerDelegate> actionPickerDelegate(instance: @ForEffect T): Set<ActionPickerDelegate> = setOf(instance)
    }
}

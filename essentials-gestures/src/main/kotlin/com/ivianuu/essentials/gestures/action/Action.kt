package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
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

@FunBinding
inline fun choosePermissions(
    permissions: ActionPermissions,
    block: @Assisted ActionPermissions.() -> List<Permission>,
): List<Permission> = permissions.block()

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

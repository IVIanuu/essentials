package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.GivenSet
import com.ivianuu.injekt.GivenSetElements
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
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

@Effect
annotation class GivenAction {
    @GivenSet
    companion object {
        @GivenSetElements
        operator fun <T : () -> Action> invoke(): Set<() -> Action> = setOf(given<T>())
    }
}

@Reader
internal inline fun permissions(block: ActionPermissions.() -> List<Permission>) =
    given<ActionPermissions>().block()

internal operator fun Permission.plus(other: Permission) = listOf(this, other)

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
}

@Effect
annotation class GivenActionFactory {
    @GivenSet
    companion object {
        @GivenSetElements
        operator fun <T : ActionFactory> invoke(): Set<ActionFactory> = setOf(given<T>())
    }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(): ActionPickerResult?
}

@Effect
annotation class GivenActionPickerDelegate {
    @GivenSet
    companion object {
        @GivenSetElements
        operator fun <T : ActionPickerDelegate> invoke(): Set<ActionPickerDelegate> =
            setOf(given<T>())
    }
}

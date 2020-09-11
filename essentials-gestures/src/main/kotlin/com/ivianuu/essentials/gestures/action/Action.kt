package com.ivianuu.essentials.gestures.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter
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

@Adapter
annotation class GivenAction {
    companion object : Adapter.Impl<() -> Action> {
        override fun ContextBuilder.configure(
            key: Key<() -> Action>,
            provider: @Reader () -> () -> Action
        ) {
            set<() -> Action> {
                add(key, elementProvider = provider)
            }
        }
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

@Adapter
annotation class GivenActionFactory {
    companion object : Adapter.Impl<ActionFactory> {
        override fun ContextBuilder.configure(
            key: Key<ActionFactory>,
            provider: @Reader () -> ActionFactory
        ) {
            set<ActionFactory> {
                add(key, elementProvider = provider)
            }
        }
    }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(): ActionPickerResult?
}

@Adapter
annotation class GivenActionPickerDelegate {
    companion object : Adapter.Impl<ActionPickerDelegate> {
        override fun ContextBuilder.configure(
            key: Key<ActionPickerDelegate>,
            provider: @Reader () -> ActionPickerDelegate
        ) {
            set<ActionPickerDelegate> {
                add(key, elementProvider = provider)
            }
        }
    }
}

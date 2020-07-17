package com.ivianuu.essentials.gestures.action

import androidx.compose.Composable
import androidx.compose.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow

@Immutable
data class Action(
    val key: String,
    val title: String,
    val permissions: List<Permission> = emptyList(),
    val unlockScreen: Boolean = false,
    val iconProvider: ActionIconProvider,
    val executor: ActionExecutor,
    val enabled: Boolean = true
)

@Effect
annotation class BindAction {
    companion object {
        @SetElements(ApplicationComponent::class)
        @Reader
        operator fun <T : () -> Action> invoke(): Set<() -> Action> = setOf(given<T>())
    }
}

interface ActionIconProvider {
    val icon: Flow<@Composable () -> Unit>
}

interface ActionExecutor {
    suspend operator fun invoke()
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
annotation class BindActionFactory {
    companion object {
        @SetElements(ApplicationComponent::class)
        @Reader
        operator fun <T : ActionFactory> invoke(): Set<ActionFactory> = setOf(given<T>())
    }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(): ActionPickerResult?
}

@Effect
annotation class BindActionPickerDelegate {
    companion object {
        @SetElements(ApplicationComponent::class)
        @Reader
        operator fun <T : ActionPickerDelegate> invoke(): Set<ActionPickerDelegate> =
            setOf(given<T>())
    }
}

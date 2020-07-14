package com.ivianuu.essentials.gestures.action

import androidx.compose.Composable
import androidx.compose.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
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

interface ActionIconProvider {
    val icon: Flow<@Composable () -> Unit>
}

interface ActionExecutor {
    suspend operator fun invoke()
}

inline fun bindAction(block: () -> Action) = setOf(block())

@Reader
internal inline fun permissions(block: ActionPermissions.() -> List<Permission>) =
    given<ActionPermissions>().block()

internal operator fun Permission.plus(other: Permission) = listOf(this, other)

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
}

inline fun bindActionFactory(block: () -> ActionFactory) = setOf(block())

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(navigator: Navigator): ActionPickerResult?
}

inline fun bindActionPickerDelegate(block: () -> ActionPickerDelegate) = setOf(block())

package com.ivianuu.essentials.gestures.action

import androidx.compose.Composable
import androidx.compose.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.set
import com.ivianuu.injekt.transient
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

@Module
fun <T : Action, F : Function<T>> action(provider: F) {
    transient(provider)
    set<Action> { add<T>() }
}

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
}

@Module
fun <T : ActionFactory> actionFactory() {
    set<ActionFactory> { add<T>() }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(navigator: Navigator): ActionPickerResult?
}

@Module
fun <T : ActionPickerDelegate> actionPickerDelegate() {
    set<ActionPickerDelegate> { add<T>() }
}

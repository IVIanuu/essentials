package com.ivianuu.essentials.gestures.action

import androidx.compose.Composable
import androidx.compose.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.KeyOverload
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.common.set
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.keyOf
import kotlinx.coroutines.flow.Flow

@Immutable
data class Action(
    val key: String,
    val title: String,
    val permissions: List<Permission> = emptyList(),
    val unlockScreen: Boolean,
    val iconProvider: ActionIconProvider,
    val executor: ActionExecutor
)

interface ActionIconProvider {
    val icon: Flow<@Composable () -> Unit>
}

interface ActionExecutor {
    suspend operator fun invoke()
}

fun ComponentBuilder.action(
    key: String,
    title: Component.() -> String,
    permissions: Component.() -> List<Permission> = { emptyList() },
    unlockScreen: Component.() -> Boolean = { false },
    iconProvider: Component.() -> ActionIconProvider,
    executor: Component.() -> ActionExecutor
) {
    action(key = key) {
        Action(
            key = key,
            title = title(),
            permissions = permissions(),
            unlockScreen = unlockScreen(),
            iconProvider = iconProvider(),
            executor = executor()
        )
    }
}

fun ComponentBuilder.action(
    key: String,
    provider: Component.() -> Action
) {
    val actionKey = keyOf<Action>(qualifier = ActionQualifier(key))
    factory(key = actionKey) { provider() }
    map<String, Action> {
        put(entryKey = key, entryValueKey = actionKey)
    }
}

data class ActionQualifier(val key: String) : Qualifier.Element

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
}

@KeyOverload
fun <T : ActionFactory> ComponentBuilder.bindActionFactoryIntoSet(
    factoryKey: Key<T>
) {
    set<ActionFactory> { add(factoryKey) }
}

interface ActionPickerDelegate {
    val title: String
    val icon: @Composable () -> Unit
    suspend fun getResult(navigator: NavigatorState): ActionPickerResult?
}

@KeyOverload
fun <T : ActionPickerDelegate> ComponentBuilder.bindActionPickerDelegateIntoSet(
    delegateKey: Key<T>
) {
    set<ActionPickerDelegate> { add(delegateKey) }
}

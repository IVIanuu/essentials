package com.ivianuu.essentials.gestures.action

import androidx.compose.Immutable
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.ui.painter.Renderable
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ModuleBuilder
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
    val icon: Flow<Renderable>
}

interface ActionExecutor {
    suspend operator fun invoke()
}

fun ModuleBuilder.bindAction(
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

fun ModuleBuilder.action(
    key: String,
    definition: Component.() -> Action
) {
    factory(name = key) { definition() }
        .intoMap<String, Action>(entryKey = key)
}

interface ActionFactory {
    fun handles(key: String): Boolean
    suspend fun createAction(key: String): Action
}

inline fun <reified T : ActionFactory> ModuleBuilder.bindActionFactory() {
    withBinding<T> { intoSet<ActionFactory>() }
}

interface ActionPickerDelegate {
    val title: String
    val icon: Renderable
    suspend fun getResult(navigator: NavigatorState): ActionPickerResult?
}

inline fun <reified T : ActionPickerDelegate> ModuleBuilder.bindActionPickerDelegate() {
    withBinding<T> { intoSet<ActionPickerDelegate>() }
}

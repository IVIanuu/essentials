package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun getActions(
    actions: Set<Action>,
    defaultDispatcher: DefaultDispatcher,
): List<Action> = withContext(defaultDispatcher) { actions.toList() }

@FunBinding
suspend fun getAction(
    actions: Set<Action>,
    actionFactories: () -> Set<ActionFactory>,
    defaultDispatcher: DefaultDispatcher,
    key: @Assisted String,
): Action = withContext(defaultDispatcher) {
    actions
        .firstOrNull { it.key == key }
        ?: actionFactories()
            .firstOrNull { it.handles(key) }
            ?.createAction(key)
        ?: error("Unsupported action key $key")
}
package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun getActions(
    actions: Set<Action>,
    dispatchers: AppCoroutineDispatchers,
): List<Action> = withContext(dispatchers.default) { actions.toList() }

@FunBinding
suspend fun getAction(
    actions: Set<Action>,
    actionFactories: () -> Set<ActionFactory>,
    dispatchers: AppCoroutineDispatchers,
    key: @Assisted String,
): Action = withContext(dispatchers.default) {
    actions
        .firstOrNull { it.key == key }
        ?: actionFactories()
            .firstOrNull { it.handles(key) }
            ?.createAction(key)
        ?: error("Unsupported action key $key")
}
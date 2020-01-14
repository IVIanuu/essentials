package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Provider
import kotlinx.coroutines.withContext

/**
 * Store for actions
 */
@Factory
class ActionStore(
    private val actions: Map<String, Provider<Action>>,
    private val actionFactories: Set<Provider<ActionFactory>>,
    private val dispatchers: AppDispatchers
) {

    suspend fun getActions(): List<Action> = actions.map { it.value() }

    suspend fun getAction(key: String): Action = withContext(dispatchers.default) {
        actions[key]?.invoke()
            ?: actionFactories
                .map { it() }
                .firstOrNull { it.handles(key) }
                ?.createAction(key)
            ?: error("Unsupported action key $key")
    }
}

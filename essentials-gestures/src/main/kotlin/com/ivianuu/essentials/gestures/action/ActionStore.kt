package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Unscoped
import kotlinx.coroutines.withContext

/**
 * Store for actions
 */
@Unscoped
class ActionStore(
    private val actions: Set<@Provider () -> Action>,
    private val actionFactories: Set<@Provider () -> ActionFactory>,
    private val dispatchers: AppCoroutineDispatchers
) {

    suspend fun getActions(): List<Action> = withContext(dispatchers.default) {
        actions.map { it() }
    }

    suspend fun getAction(key: String): Action = withContext(dispatchers.default) {
        actions
            .map { it() }
            .firstOrNull { it.key == key }
            ?: actionFactories
                .map { it() }
                .firstOrNull { it.handles(key) }
                ?.createAction(key)
            ?: error("Unsupported action key $key")
    }
}

package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext

@Reader
suspend fun getActions(): List<Action> = withContext(dispatchers.default) {
    given<Set<() -> Action>>()
        .map { it() }
}

@Reader
suspend fun getAction(key: String): Action = withContext(dispatchers.default) {
    given<Set<() -> Action>>()
        .map { it() }
        .firstOrNull { it.key == key }
        ?: given<Set<() -> ActionFactory>>()
            .map { it() }
            .firstOrNull { it.handles(key) }
            ?.createAction(key)
        ?: error("Unsupported action key $key")
}

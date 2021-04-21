package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Given
fun popToRootOnActivityDestroyWorker(@Given navigator: Navigator): ScopeWorker<UiGivenScope> = {
    runOnCancellation {
        navigator.state
            .first()
            .backStack
            .drop(1)
            .filterIsInstance<Key<Any>>()
            .forEach { navigator.pop(it) }
    }
}

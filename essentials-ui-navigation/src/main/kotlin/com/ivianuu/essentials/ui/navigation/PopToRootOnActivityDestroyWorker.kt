package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.ui.UiGivenScope
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.first

@Given
fun popToRootOnActivityDestroyWorker(
    @Given navigator: Navigator
): ScopeWorker<UiGivenScope> = {
    runOnCancellation {
        navigator
            .first()
            .backStack
            .drop(1)
            .filterIsInstance<Key<Any>>()
            .forEach { navigator.pop(it) }
    }
}

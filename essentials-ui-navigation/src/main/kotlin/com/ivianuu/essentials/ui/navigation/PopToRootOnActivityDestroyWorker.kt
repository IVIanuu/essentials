package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.UiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Given
fun popToRootOnActivityDestroyWorker(
    @Given navigationState: Flow<NavigationState>,
    @Given navigator: Collector<NavigationAction>
): ScopeWorker<UiGivenScope> = {
    runOnCancellation {
        navigationState
            .first()
            .backStack
            .drop(1)
            .filterIsInstance<Key<Any>>()
            .forEach { navigator(Pop(it)) }
    }
}

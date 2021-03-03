package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.UiWorker
import com.ivianuu.essentials.ui.UiWorkerBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction.Pop
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@UiWorkerBinding
@Given
fun popToRootOnActivityDestroyWorker(
    @Given navigationState: Flow<NavigationState>,
    @Given navigator: DispatchAction<NavigationAction>
): UiWorker = {
    runOnCancellation {
        navigationState
            .first()
            .backStack
            .drop(1)
            .filterIsInstance<Key<Any>>()
            .forEach { navigator(Pop(it)) }
    }
}

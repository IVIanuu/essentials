package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.UiWorker
import com.ivianuu.essentials.ui.UiWorkerBinding
import com.ivianuu.essentials.ui.navigation.NavigationAction.Clear
import com.ivianuu.injekt.Given

@UiWorkerBinding
@Given
fun clearBackStackOnActivityDestroyWorker(
    @Given navigator: DispatchAction<NavigationAction>
): UiWorker = {
    runOnCancellation {
        navigator(Clear)
    }
}

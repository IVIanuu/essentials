package com.ivianuu.essentials.ui.director

import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.effect.coroutineScope
import com.ivianuu.essentials.ui.effect.onActive
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.injekt.getMvRxViewModel
import com.ivianuu.injekt.ParametersDefinition
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <reified T : MvRxViewModel<*>> ControllerContext.mvrxViewModel(
        noinline parameters: ParametersDefinition? = null
): T {
    val viewModel = controller.getMvRxViewModel<EsController, T>(parameters = parameters)
    val coroutineScope = coroutineScope()
    onActive {
        viewModel.flow
                .onEach { invalidate() }
                .launchIn(coroutineScope)
    }
    return viewModel
}
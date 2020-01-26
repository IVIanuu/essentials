package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.KeyboardHide
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get

internal val EsInputMethodActionModule = Module {
    bindAction(
        key = "input_method",
        title = { getStringResource(R.string.es_action_input_method) },
        iconProvider = { SingleActionIconProvider(Icons.Default.KeyboardHide) },
        executor = { get<InputMethodActionExecutor>() }
    )
}

@Factory
internal class InputMethodActionExecutor(
    private val inputMethodManager: InputMethodManager
) : ActionExecutor {
    override suspend fun invoke() {
        inputMethodManager.showInputMethodPicker()
    }
}

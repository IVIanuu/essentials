package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.KeyboardHide
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory

internal fun ComponentBuilder.inputMethodAction() {
    action(
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

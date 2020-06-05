package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn

@Module
private fun InputMethodModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("input_method") Action>(
        key = "input_method",
        title = { getStringResource(R.string.es_action_input_method) },
        iconProvider = { SingleActionIconProvider(Icons.Default.KeyboardHide) },
        executor = { get<InputMethodActionExecutor>() }
    )*/
}

@Transient
internal class InputMethodActionExecutor(
    private val inputMethodManager: InputMethodManager
) : ActionExecutor {
    override suspend fun invoke() {
        inputMethodManager.showInputMethodPicker()
    }
}

package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.KeyboardHide
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@BindAction
@Reader
fun inputMethodAction() = Action(
    key = "input_method",
    title = Resources.getString(R.string.es_action_input_method),
    iconProvider = SingleActionIconProvider(Icons.Default.KeyboardHide),
    executor = given<InputMethodActionExecutor>()
)

@Given
internal class InputMethodActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        given<InputMethodManager>().showInputMethodPicker()
    }
}

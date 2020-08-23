package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.given

@GivenAction
fun inputMethodAction() = Action(
    key = "input_method",
    title = Resources.getString(R.string.es_action_input_method),
    icon = singleActionIcon(R.drawable.es_ic_keyboard_hide),
    execute = {
        given<InputMethodManager>().showInputMethodPicker()
    }
)

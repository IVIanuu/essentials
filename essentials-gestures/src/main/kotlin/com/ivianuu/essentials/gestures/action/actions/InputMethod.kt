package com.ivianuu.essentials.gestures.action.actions

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.util.Resources

@ActionBinding
fun inputMethodAction(
    inputMethodManager: InputMethodManager,
    resources: Resources,
): Action = Action(
    key = "input_method",
    title = resources.getString(R.string.es_action_input_method),
    icon = singleActionIcon(R.drawable.es_ic_keyboard_hide),
    execute = { inputMethodManager.showInputMethodPicker() }
)
